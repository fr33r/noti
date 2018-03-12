package api.representations.json;

import api.representations.Representation;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

public final class Target extends Representation {

	private final UUID uuid;
	private final String name;
	private final String phoneNumber;
	
	public Target() {
		super(MediaType.APPLICATION_JSON_TYPE);

		this.uuid = null;
		this.name = null;
		this.phoneNumber = null;
	}
	
	public Target(final String name, final String phoneNumber) {
		super(MediaType.APPLICATION_JSON_TYPE);

		this.uuid = null;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}
	
	public Target(final UUID uuid, final String name, final String phoneNumber) {
		super(MediaType.APPLICATION_JSON_TYPE);

		this.uuid = uuid;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}
	
	public UUID getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
}
