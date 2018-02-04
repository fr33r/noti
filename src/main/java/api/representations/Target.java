package api.representations;

import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import api.representations.Tag;

public class Target {

	private final UUID uuid;
	private final String name;
	private final String phoneNumber;
	private final Set<Tag> tags;
	
	public Target() {
		this.uuid = null;
		this.name = null;
		this.phoneNumber = null;
		this.tags = new HashSet<>();
	}
	
	public Target(final String name, final String phoneNumber) {
		this.uuid = null;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.tags = new HashSet<>();
	}
	
	public Target(final UUID uuid, final String name, final String phoneNumber) {
		this.uuid = uuid;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.tags = new HashSet<>();
	}
	
	public Target(final String name, final String phoneNumber, final Set<Tag> tags) {
		this.uuid = null;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.tags = tags;
	}
	
	public Target(final UUID uuid, final String name, final String phoneNumber, final Set<Tag> tags) {
		this.uuid = uuid;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.tags = tags;
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

	public Set<Tag> getTags() {
		return tags;
	}
}
