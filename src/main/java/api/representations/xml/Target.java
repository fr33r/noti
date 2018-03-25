package api.representations.xml;

import api.representations.Representation;

import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "target")
public final class Target extends Representation {

	private final UUID uuid;
	private final String name;
	private final String phoneNumber;

	public Target() {
		super(MediaType.APPLICATION_XML_TYPE);

		this.uuid = null;
		this.name = null;
		this.phoneNumber = null;
	}

	public Target(final String name, final String phoneNumber) {
		super(MediaType.APPLICATION_XML_TYPE);

		this.uuid = null;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}
	
	public Target(final UUID uuid, final String name, final String phoneNumber) {
		super(MediaType.APPLICATION_XML_TYPE);

		this.uuid = uuid;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	@XmlElement
	public UUID getUUID() {
		return uuid;
	}

	@XmlElement
	public String getName() {
		return name;
	}

	@XmlElement
	public String getPhoneNumber() {
		return phoneNumber;
	}
}
