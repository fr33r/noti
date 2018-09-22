package infrastructure.services;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import domain.Message;
import domain.Notification;
import infrastructure.MessageQueueService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;

@Service
public final class SMSQueueService implements MessageQueueService {

  /** Represents a callback that is invoked after an attempt to send a message to Kafka. */
  private class ProducerCallback implements Callback {

    private static final String SMS_ENQUEUE_SUCCESS_METER_NAME = "sms.enqueue.success";
    private static final String SMS_ENQUEUE_FAILURE_METER_NAME = "sms.enqueue.failure";

    private final MetricRegistry metricRegistry;
    private final Meter enqueueSuccessMeter;
    private final Meter enqueueFailureMeter;
    private final Logger logger;

    /**
     * Constructs a {@link ProducerCallback}.
     *
     * @param metricRegistry The metric registry use to record metrics.
     */
    public ProducerCallback(final MetricRegistry metricRegistry, final Logger logger) {
      this.metricRegistry = metricRegistry;
      this.enqueueSuccessMeter =
          this.metricRegistry.meter(
              MetricRegistry.name(SMSQueueService.class, SMS_ENQUEUE_SUCCESS_METER_NAME));
      this.enqueueFailureMeter =
          this.metricRegistry.meter(
              MetricRegistry.name(SMSQueueService.class, SMS_ENQUEUE_FAILURE_METER_NAME));
      this.logger = logger;
    }

    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception x) {
      if (x != null) {
        this.enqueueFailureMeter.mark();
        logger.error("Unable to send message to Kafka.", x);
      } else {
        this.enqueueSuccessMeter.mark();
        logger.debug("Successfully sent message to Kafka.", recordMetadata);
        logger.info("Successfully enqueued SMS message.");
      }
    }
  }

  private final Producer<String, GenericRecord> producer;
  private final MetricRegistry metricRegistry;
  private final Logger logger;
  private static final String TOPIC_NAME = "sms";

  @Inject
  public SMSQueueService(
      Producer<String, GenericRecord> producer,
      MetricRegistry metricRegistry,
      @Named("infrastructure.services.SMSQueueService") Logger logger) {
    this.producer = producer;
    this.metricRegistry = metricRegistry;
    this.logger = logger;
  }

  private String getSchema() {
    StringBuilder builder = new StringBuilder();
    builder
        .append("{")
        .append("\"namespace\": \"noti\",")
        .append("\"type\":\"record\",")
        .append("\"name\":\"Message\",")
        .append("\"fields\":")
        .append("[")
        .append("{")
        .append("\"name\":\"notificationUUID\",")
        .append("\"type\":\"string\"")
        .append("},")
        .append("{")
        .append("\"name\":\"messageID\",")
        .append("\"type\":\"int\"")
        .append("}")
        .append("]")
        .append("}");
    String schemaString = builder.toString();
    return schemaString;
  }

  @Override
  public void send(Notification notification, Integer messageID)
      throws InterruptedException, ExecutionException, TimeoutException {

    if (notification == null) {
      throw new IllegalArgumentException("The argument 'notification' cannot be null.");
    }

    if (messageID == null) {
      throw new IllegalArgumentException("The argument 'messageID' cannot be null.");
    }

    // ensure the message ID provided is valid.
    Message message = notification.message(messageID);
    if (message == null) {
      throw new IllegalArgumentException(
          "Invalid value for 'messageID'. The message does not exist.");
    }

    // construct the Avro record.
    String schemaString = this.getSchema();
    Schema.Parser parser = new Schema.Parser();
    Schema schema = parser.parse(schemaString);

    GenericRecord messageRecord = new GenericData.Record(schema);
    messageRecord.put("notificationUUID", notification.getId().toString());
    messageRecord.put("messageID", messageID);

    // construct record to send to Kafka.
    String key =
        message.getTo().toE164(); // choosing the target phone number to preserve order of messages.
    ProducerRecord<String, GenericRecord> record =
        new ProducerRecord<String, GenericRecord>(TOPIC_NAME, key, messageRecord);

    // although it would be ideal to have this be purely asynchronous,
    // the issue lies in being able to adequately recover from a failure.
    // the following are the main issues at hand that should be solved:
    // - if sending to Kafka fails and we are purely asynchronous, how do we
    //   update the message's status to FAILED? how do we guarantee that
    //   back up strategy succeeds? what dependencies are sensible for this service
    //   and its callback?
    // - we would prefer to fail fast, meaning that a failure to send the
    //   message to Kafka would ideally be surfaced to the caller as soon
    //   as possible, as opposed to the caller needing to check in every so
    //   often to check the status of the message. the counter-argument is that
    //   these errors would be so infrequent, that it doesn't warrant the extra
    //   latency, and the client would not poll because a failure is so rare.
    // choosing to do the call to Kafka synchronously with a tight timeout is
    // a solid middle ground, because it keeps latency a top priority, while also
    // providing the ability to surface errors immediately to clients.
    Future<RecordMetadata> future =
        this.producer.send(record, new ProducerCallback(this.metricRegistry, this.logger));
    future.get(100, TimeUnit.MILLISECONDS);
  }
}
