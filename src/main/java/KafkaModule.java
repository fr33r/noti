import configuration.KafkaConfiguration;
import configuration.KafkaProducerConfiguration;
import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;
import java.util.Properties;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public final class KafkaModule extends NotiModule {

  // Kafka properties.
  private static final String KAFKA_PROPERTY_BOOTSTRAP_SERVERS = "bootstrap.servers";
  private static final String KAFKA_PROPERTY_ACKS = "acks";
  private static final String KAFKA_PROPERTY_SCHEMA_REGISTRY_URL = "schema.registry.url";
  private static final String KAFKA_PROPERTY_KEY_SERIALIZER = "key.serializer";
  private static final String KAFKA_PROPERTY_VALUE_SERIALIZER = "value.serializer";
  private static final String KAFKA_PROPERTY_CLIENT_ID = "client.id";

  // Kafka property values.
  private static final String KEY_SERIALIZER = "io.confluent.kafka.serializers.KafkaAvroSerializer";
  private static final String VALUE_SERIALIZER =
      "io.confluent.kafka.serializers.KafkaAvroSerializer";
  private static final String CLIENT_ID = "noti-producer";

  public KafkaModule(NotiConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {

    // extract the configuration.
    KafkaConfiguration kafkaConfiguration = this.getConfiguration().getKafkaConfiguration();
    KafkaProducerConfiguration producerConfiguration =
        kafkaConfiguration.getProducerConfiguration();

    // configure the Kafka producer.
    Properties producerProperties = new Properties();
    producerProperties.put(
        KAFKA_PROPERTY_BOOTSTRAP_SERVERS,
        String.join(",", producerConfiguration.getBootstrapServers()));
    producerProperties.put(KAFKA_PROPERTY_ACKS, producerConfiguration.getAcks());
    producerProperties.put(
        KAFKA_PROPERTY_SCHEMA_REGISTRY_URL, producerConfiguration.getSchemaRegistryURL());
    producerProperties.put(KAFKA_PROPERTY_CLIENT_ID, CLIENT_ID);
    producerProperties.put(KAFKA_PROPERTY_KEY_SERIALIZER, KEY_SERIALIZER);
    producerProperties.put(KAFKA_PROPERTY_VALUE_SERIALIZER, VALUE_SERIALIZER);

    // construct the Kafka producer.
    Producer<String, GenericRecord> producer =
        new KafkaProducer<String, GenericRecord>(producerProperties);

    // register Kafka producer with environment.
    AbstractBinder binder =
        new AbstractBinder() {
          @Override
          protected void configure() {
            this.bind(producer).to(new TypeLiteral<Producer<String, GenericRecord>>() {});
          }
        };
    this.getEnvironment().jersey().register(binder);
  }
}
