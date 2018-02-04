package domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 */
public class Notification extends Entity<UUID> implements Cloneable{

	private String content;
	private NotificationStatus status;
	private Date sendAt;
	private Date sentAt;
	private Set<Target> targets;
	private Set<Message> messages;
	private NotificationState state;
	
	//need to separate this out of the notification class.
	//only created a builder because 1) eric evans blessing and 2) constructors were getting wild.
	public static class Builder extends EntityBuilder<Notification>{
		
		private String content;
		private UUID uuid;
		private NotificationStatus status;
		private Date sendAt;
		private Date sentAt;
		private Set<Target> targets;
		private Set<Message> messages;
		
		public Builder content(String content) {
			this.content = content;
			return this;
		}
		
		public Builder identity(UUID uuid) {
			this.uuid = uuid;
			return this;
		}
		
		public Builder identity(String uuidString) {
			this.uuid = UUID.fromString(uuidString);
			return this;
		}
		
		public Builder status(NotificationStatus status) {
			this.status = status;
			return this;
		}
		
		public Builder sendAt(Date sendAt) {
			this.sendAt = sendAt;
			return this;
		}
		
		public Builder sentAt(Date sentAt) {
			this.sentAt = sentAt;
			return this;
		}
		
		public Builder target(Target target) {
			if(this.targets == null) {
				this.targets = new HashSet<>();
			}
			this.targets.add(target);
			return this;
		}
		
		public Builder targets(Target...targets) {
			for(Target target : targets) {
				this.target(target);
			}
			return this;
		}
		
		public Builder targets(Set<Target> targets) {
			this.targets = targets;
			return this;
		}
		
		public Builder message(Message message) {
			if(this.messages == null) {
				this.messages = new HashSet<>();
			}
			this.messages.add(message);
			return this;
		}
		
		public Builder messages(Message...messages) {
			for(Message message : messages) {
				this.message(message);
			}
			return this;
		}
		
		public Builder messages(Set<Message> messages) {
			this.messages = messages;
			return this;
		}
		
		@Override
		public Notification build() {
			UUID uuid = this.uuid == null ? UUID.randomUUID() : this.uuid;
			return new Notification(uuid, this.content, this.status, this.targets, this.sendAt, this.sentAt);
		}
	}
	
	public Notification(UUID uuid, String content, NotificationStatus status, Date sendAt) {
		super(uuid);
		this.content = content;
		this.status = status;
		this.sendAt = sendAt;
		this.sentAt = null;
		this.targets = new HashSet<>();
		this.messages = new HashSet<>();
	}
	
	public Notification(UUID uuid, String content, NotificationStatus status, Date sendAt, Date sentAt) {
		this(uuid, content, status, sendAt);
	}
	
	public Notification(UUID uuid, String content, NotificationStatus status, Set<Target> targets, Date sendAt, Date sentAt) {
		this(uuid, content, status, targets, sendAt);
	}

	public Notification(UUID uuid, String content, NotificationStatus status, Set<Target> targets, Date sendAt) {
		super(uuid);
		this.content = content;
		this.status = status;
		this.sendAt = sendAt;
		this.sentAt = null;
		this.targets = targets;
	}

	public Notification(Notification notification) {
		this.content = notification.getContent();
		this.status = notification.getStatus();
		this.sendAt = notification.getSendAt();
		this.sentAt = notification.getSentAt();
		this.targets = notification.getTargets();
		this.messages = notification.getMessages();
	}

	@Override
	public boolean isAggregateRoot() {
		return true;
	}
	
	void setState(NotificationState state) {
		this.state = state;
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public String getContent() {
		return this.content;
	}

	public Set<Target> getTargets(){
		Set<Target> targetsCopy = new HashSet<>();
		for(Target target : this.targets){
			targetsCopy.add((Target)target.clone());
		}
		return targetsCopy;
	}

	public void addTarget(Target target) {
		this.targets.add(target);
	}

	public NotificationStatus getStatus() {
		return this.status;
	}
	
	void setStatus(NotificationStatus status) {
		this.status = status;
	}

	public Date getSendAt(){
		if(this.sendAt == null ) { return null; }
		return (Date)this.sendAt.clone();
	}

	public Date getSentAt(){
		if(this.sentAt == null) { return null; }
		return (Date)this.sentAt.clone();
	}

	protected void setSentAt(Date sentAt) {
		this.sentAt = sentAt;
	}

	@Override
	public Object clone() {
		Notification notification = null;

		try {
			notification = (Notification)super.clone();

			if (notification.sendAt != null) {
				notification.sendAt = (Date)notification.sendAt.clone();
			}

			if (notification.sentAt != null) {
				notification.sentAt = (Date)notification.sentAt.clone();
			}

			notification.targets = notification.getTargets();

		} catch (CloneNotSupportedException ex) { 
			//not possible.
		}

		return notification;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		final String nu11 = "null";
		String uuid = this.getId() == null ? nu11 : this.getId().toString();
		String message = this.getContent();
		String status = this.getStatus().toString();
		String sendAt = this.getSendAt() == null ? nu11 : this.getSendAt().toString();
		String sentAt = this.getSentAt() == null ? nu11 : this.getSentAt().toString();

		builder
			.append("[")
			.append("uuid=").append(uuid).append(", ")
			.append("message=").append(message).append(", ")
			.append("status=").append(status).append(", ")
			.append("sendAt=").append(sendAt).append(", ")
			.append("sentAt=").append(sentAt).append(", ")
			.append("]");
		return builder.toString();
	}

	public static Builder builder() {
		return new Builder();
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}
}

