package domain;

import domain.Tag;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

public class Target extends Entity<UUID> implements Cloneable{

	private String name;
	private PhoneNumber phoneNumber;
	private Set<Tag> tags;

	public Target(String name, PhoneNumber phoneNumber) {
		super();
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.tags = new HashSet<>();
	}
	
	public Target(UUID uuid, String name, PhoneNumber phoneNumber) {
		super(uuid);
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.tags = new HashSet<>();
	}
	
	public Target(String name, PhoneNumber phoneNumber, Set<Tag> tags) {
		super();
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.tags = tags;
	}

	public Target(UUID uuid, String name, PhoneNumber phoneNumber, Set<Tag> tags) {
		super(uuid);	
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.tags = tags;
	}

	public Target(Target target){
		super(target.getId());
		this.name = target.getName();
		this.phoneNumber = target.getPhoneNumber();
		this.tags = target.getTags();
	}

	@Override
	public boolean isAggregateRoot() {
		return false;
	}

	public PhoneNumber getPhoneNumber(){
		return this.phoneNumber;
	}

	public String getName() {
		return this.name;
	}

	public Set<Tag> getTags() {
		Set<Tag> tagsCopy = new HashSet<>();
		for (Tag tag : this.tags) {
			tagsCopy.add((Tag)tag.clone());
		}
		return tagsCopy;
	}

	public void tag(Tag tag) {
		if (!this.tags.add(tag)){
			throw new IllegalStateException();
		}
	}

	public void removeTag(Tag tag) {
		this.tags.remove(tag);
	}

	@Override
	public Object clone() {

		Target target = null;

		try {
			target = (Target)super.clone();
			target.tags = target.getTags();
		} catch (CloneNotSupportedException ex) {
			//not possible;
		}

		return target;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder
			.append("[")
			.append("name=").append(this.name).append(", ")
			.append("phoneNumber=").append(this.phoneNumber).append(", ")
			.append("tags=").append(this.tags.toString())
			.append("]");
		return builder.toString();
	}
}
