package api.representations.xml;

import api.representations.Representation;
import api.representations.xml.Target;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * Defines the 'application/xml' representation of an Audience resource.
 *
 * @author Jon Freer
 */
@XmlRootElement(name = "audience")
public final class Audience extends Representation {

	private UUID uuid;
	private String name;
	private Set<Target> members;

	/**
	 * Constructs an empty instance of {@link Audience}.
	 */
	private Audience() {
		super(MediaType.APPLICATION_XML_TYPE);

		this.uuid = null;
		this.name = null;
		this.members = new HashSet<>();
	}

	public static final class Builder extends Representation.Builder {

		private UUID uuid;
		private String name;
		private Set<Target> members;

		public Builder() {
			super(MediaType.APPLICATION_XML_TYPE);
			this.members = new HashSet<>();
		}

		public Builder uuid(UUID uuid) { this.uuid = uuid; return this; }

		public Builder name(String name) { this.name = name; return this; }

		public Builder addMember(Target member) { this.members.add(member); return this;  }

		public Builder members(Set<Target> members) { this.members = members; return this; }

		@Override
		public Representation build() {
			Audience a = new Audience();
			a.setLocation(this.location());
			a.setEncoding(this.encoding());
			a.setLanguage(this.language());
			a.setLastModified(this.lastModified());
			a.setUUID(this.uuid);
			a.setName(this.name);
			a.setMembers(this.members);
			return a;
		}
	}

	/**
	 * Retrieves the universally unique identifier for this audience.
	 *
	 * @return The universally unique identifier for this audience.
	 */
	@XmlElement
	public UUID getUUID() { return this.uuid; }

	private void setUUID(UUID uuid) { this.uuid = uuid; }

	/**
	 * Retrieves the name of the audience.
	 *
	 * @return The name of the audience.
	 */
	@XmlElement
	public String getName() { return this.name; }

	private void setName(String name) { this.name = name; }

	/**
	 * Retrieves the members that collectively resemble this audience.
	 *
	 * @return The members that collectively resemble this audience.
	 */
	@XmlElementWrapper(name="members")
	@XmlElement(name="member")
	public Set<Target> getMembers() { return this.members; }

	private void setMembers(Set<Target> members) { this.members = members; }

	/**
	 * Determines if the provided instance is equal to the calling instance.
	 *
	 * @param obj The instance to compare to the calling instance for equality.
	 * @return {@code true} if the two instances are equal; {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null || this.getClass() != obj.getClass()) return false;

		Audience audience = (Audience)obj;
		boolean nameIsEqual =
			(this.name == null && audience.name == null) ||
			(this.name != null && audience.name != null && this.name.equals(audience.name));
		boolean membersAreEqual = this.members.equals(audience.members);

		return nameIsEqual && membersAreEqual;
	}

	/**
	 * Generates hash code for this instance.
	 *
	 * @return The hash code represented as an integer.
	 */
	@Override
	public int hashCode() {
		int hashCode = 1;
		final int prime = 17;

		if(this.name != null) {
			hashCode = hashCode * prime + this.name.hashCode();
		}

		if(this.members != null) {
			hashCode = hashCode * prime + this.members.hashCode();
		}

		return hashCode;
	}
}
