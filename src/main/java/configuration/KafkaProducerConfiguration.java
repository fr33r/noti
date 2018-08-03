package configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class KafkaProducerConfiguration {

  private String acks;
  private List<String> bootstrapServers;
  private String schemaRegistryURL;

  @JsonProperty("acks")
  public String getAcks() {
    return this.acks;
  }

  @JsonProperty("acks")
  public void setAcks(String acks) {
    this.acks = acks;
  }

  @JsonProperty("bootstrap.servers")
  public List<String> getBootstrapServers() {
    return this.bootstrapServers;
  }

  @JsonProperty("bootstrap.servers")
  public void setBootstrapServers(List<String> bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  @JsonProperty("schema.registry.url")
  public String getSchemaRegistryURL() {
    return this.schemaRegistryURL;
  }

  @JsonProperty("schema.registry.url")
  public void setSchemaRegistryURL(String schemaRegistryURL) {
    this.schemaRegistryURL = schemaRegistryURL;
  }
}
