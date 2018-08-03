package configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents the Apache Kafka configuration for Noti. */
public final class KafkaConfiguration {

  private KafkaProducerConfiguration producerConfiguration;

  @JsonProperty("producer")
  public KafkaProducerConfiguration getProducerConfiguration() {
    return this.producerConfiguration;
  }

  @JsonProperty("producer")
  public void setProducerConfiguration(KafkaProducerConfiguration producerConfiguration) {
    this.producerConfiguration = producerConfiguration;
  }
}
