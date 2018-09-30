package configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents the database configuration for Noti. */
public class DatabaseConfiguration {

  private String host;
  private int port;
  private String name;
  private String user;
  private String password;
  private boolean useSSL;
  private boolean useLegacyDatetimeCode;
  private boolean allowPublicKeyRetrieval;

  @JsonProperty
  public String getHost() {
    return this.host;
  }

  @JsonProperty
  public void setHost(String host) {
    this.host = host;
  }

  @JsonProperty
  public int getPort() {
    return this.port;
  }

  @JsonProperty
  public void setPort(int port) {
    this.port = port;
  }

  @JsonProperty
  public String getName() {
    return this.name;
  }

  @JsonProperty
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty
  public String getUser() {
    return this.user;
  }

  @JsonProperty
  public void setUser(String user) {
    this.user = user;
  }

  @JsonProperty
  public String getPassword() {
    return this.password;
  }

  @JsonProperty
  public void setPassword(String password) {
    this.password = password;
  }

  @JsonProperty
  public boolean getUseSSL() {
    return this.useSSL;
  }

  @JsonProperty
  public void setUseSSL(boolean useSSL) {
    this.useSSL = useSSL;
  }

  @JsonProperty
  public boolean getUseLegacyDatetimeCode() {
    return this.useLegacyDatetimeCode;
  }

  @JsonProperty
  public void setUseLegacyDatetimeCode(boolean useLegacyDatetimeCode) {
    this.useLegacyDatetimeCode = useLegacyDatetimeCode;
  }

  @JsonProperty
  public boolean getAllowPublicKeyRetrieval() {
    return this.allowPublicKeyRetrieval;
  }

  @JsonProperty
  public void setAllowPublicKeyRetrieval(boolean allowPublicKeyRetrieval) {
    this.allowPublicKeyRetrieval = allowPublicKeyRetrieval;
  }

  @JsonIgnore
  public String getURL() {

    // retrieve configuration values.
    final String host = this.getHost();
    final Integer port = this.getPort();
    final String databaseName = this.getName();
    final Boolean useLegacyDateTimeCode = this.getUseLegacyDatetimeCode();
    final Boolean useSSL = this.getUseSSL();
    final Boolean allowPublicKeyRetrieval = this.getAllowPublicKeyRetrieval();

    // construct the URL.
    final String urlTemplate =
        "jdbc:mysql://%s:%s/%s?useLegacyDatetimeCode=%b&useSSL=%b&allowPublicKeyRetrieval=%b";
    return String.format(
        urlTemplate,
        host,
        port,
        databaseName,
        useLegacyDateTimeCode,
        useSSL,
        allowPublicKeyRetrieval);
  }
}
