package configuration;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *	Represents the database configuration for Noti.
 */
public class DatabaseConfiguration extends Configuration {

	private String host;
	private int port;
	private String name;
	private String user;
	private String password;
	private boolean useSSL;
	private boolean useLegacyDatetimeCode;

	@JsonProperty
	public String getHost(){
		return this.host;
	}

	@JsonProperty
	public void setHost(String host){
		this.host = host;
	}

	@JsonProperty
	public int getPort(){
		return this.port;
	}

	@JsonProperty
	public void setPort(int port){
		this.port = port;
	}

	@JsonProperty
	public String getName(){
		return this.name;
	}

	@JsonProperty
	public void setName(String name){
		this.name = name;
	}

	@JsonProperty
	public String getUser(){
		return this.user;
	}

	@JsonProperty
	public void setUser(String user){
		this.user = user;
	}

	@JsonProperty
	public String getPassword(){
		return this.password;
	}

	@JsonProperty
	public void setPassword(String password){
		this.password = password;
	}

	@JsonProperty
	public boolean getUseSSL(){
		return this.useSSL;
	}

	@JsonProperty
	public void setUseSSL(boolean useSSL){
		this.useSSL = useSSL;
	}

	@JsonProperty
	public boolean getUseLegacyDatetimeCode(){
		return this.useLegacyDatetimeCode;
	}

	@JsonProperty
	public void setUseLegacyDatetimeCode(boolean useLegacyDatetimeCode){
		this.useLegacyDatetimeCode = useLegacyDatetimeCode;
	}
}
