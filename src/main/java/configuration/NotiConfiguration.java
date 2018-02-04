package configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 *	Represents the configuration for Noti.
 */
public class NotiConfiguration extends Configuration {

	private DatabaseConfiguration databaseConfiguration;
	private SMSConfiguration smsConfiguration;
	private com.uber.jaeger.dropwizard.Configuration jaegerConfiguration;

	@JsonProperty("database")
	public DatabaseConfiguration getDatabaseConfiguration(){
		return this.databaseConfiguration;
	}

	@JsonProperty("database")
	public void setDatabaseConfiguration(final DatabaseConfiguration databaseConfiguration){
		this.databaseConfiguration = databaseConfiguration;
	}

	@JsonProperty("sms")
	public SMSConfiguration getSMSConfiguration(){
		return this.smsConfiguration;
	}

	@JsonProperty("sms")
	public void setSMSConfiguration(final SMSConfiguration smsConfiguration){
		this.smsConfiguration = smsConfiguration;
	}

	@JsonProperty("jaeger")
	public com.uber.jaeger.dropwizard.Configuration getJaegerConfiguration(){
		return this.jaegerConfiguration;
	}

	@JsonProperty("jaeger")
	public void setJaegerConfiguration(final com.uber.jaeger.dropwizard.Configuration configuration){
		this.jaegerConfiguration = configuration;
	}
}
