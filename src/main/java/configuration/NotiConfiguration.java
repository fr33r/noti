package configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

/** Represents the configuration for Noti. */
public class NotiConfiguration extends Configuration {

  private DatabaseConfiguration databaseConfiguration;
  private com.uber.jaeger.dropwizard.Configuration jaegerConfiguration;
  private KafkaConfiguration kafkaConfiguration;

  @JsonProperty("database")
  public DatabaseConfiguration getDatabaseConfiguration() {
    return this.databaseConfiguration;
  }

  @JsonProperty("database")
  public void setDatabaseConfiguration(final DatabaseConfiguration databaseConfiguration) {
    this.databaseConfiguration = databaseConfiguration;
  }

  @JsonProperty("jaeger")
  public com.uber.jaeger.dropwizard.Configuration getJaegerConfiguration() {
    return this.jaegerConfiguration;
  }

  @JsonProperty("jaeger")
  public void setJaegerConfiguration(final com.uber.jaeger.dropwizard.Configuration configuration) {
    this.jaegerConfiguration = configuration;
  }

  @JsonProperty("kafka")
  public KafkaConfiguration getKafkaConfiguration() {
    return this.kafkaConfiguration;
  }

  @JsonProperty("kafka")
  public void setKafkaConfiguration(final KafkaConfiguration configuration) {
    this.kafkaConfiguration = configuration;
  }
}
