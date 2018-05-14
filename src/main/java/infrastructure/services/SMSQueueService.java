package infrastructure.services;

import domain.Message;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.jvnet.hk2.annotations.Service;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.Schema;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import infrastructure.MessageQueueService;

@Service
public final class SMSQueueService implements MessageQueueService {

	/**
	 * Represents a callback that is invoked after an attempt to send a message to Kafka.
	 */
	private class ProducerCallback implements Callback {

		@Override
		public void onCompletion(RecordMetadata recordMetadata, Exception x) {
			if(x != null) {
				x.printStackTrace();
			} else {
				System.out.println("[TEST] Successful send to Kafka! Offset: " + recordMetadata.offset());
			}
		}
	}

	private final Producer<String, GenericRecord> producer;
	private static final String TOPIC_NAME = "sms";
	
	@Inject
	public SMSQueueService(Producer<String, GenericRecord> producer) {
		this.producer = producer;
	}

	private String getSchema() {
		StringBuilder builder = new StringBuilder();
		builder
			.append("{")
			.append("\"namespace\": \"noti\",")
			.append("\"type\":\"record\",")
			.append("\"name\":\"MessageThree\",")
			.append("\"fields\":")
			.append("[")
			.append("{")
			.append("\"name\":\"id\",") //this is probably the only field i need.
			.append("\"type\":\"int\"")
			.append("}")
			.append("]")
			.append("}");
		String schemaString = builder.toString();
		return schemaString;
	}

	@Override
	public void send(Message message) {

		//construct the Avro record.
		String schemaString = this.getSchema();
		Schema.Parser parser = new Schema.Parser();
		Schema schema = parser.parse(schemaString);

		GenericRecord messageRecord = new GenericData.Record(schema);
		messageRecord.put("id", message.getId());

		//construct record to send to Kafka.
		String key = message.getTo().toE164(); //choosing the target phone number to preserve order of messages.
		ProducerRecord<String, GenericRecord> record =
			new ProducerRecord<String, GenericRecord>(TOPIC_NAME, key, messageRecord);

		try {
			//send message to Kafka asynchronously.
			RecordMetadata m = this.producer.send(record, new ProducerCallback()).get();
			//throw new RuntimeException("[TEST] Called Kafka => " + m.offset());
		} catch(InterruptedException | ExecutionException i) {
			i.printStackTrace();
		}
	}
}
