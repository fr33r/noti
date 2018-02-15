package api.representations;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import api.representations.Target;
import api.representations.NotificationStatus;

public class Notification {

	private String uuid;
	private String message;
	private Date sentAt;
	private Date sendAt;
	private NotificationStatus status;
	private Set<Target> targets;

	public Set<Target> getTargets() {
		return targets;
	}

	public Notification() {}

	public Notification(
			String uuid,
			String message, 
			NotificationStatus status, 
			Date sendAt, 
			Date sentAt
		) {
			this.uuid = uuid;
			this.message = message;
			this.status = status;
			this.sendAt = sendAt;
			this.sentAt = sentAt;
			this.targets = new HashSet<>();
		}
	
	public Notification(
		String uuid,
		String message, 
		NotificationStatus status, 
		Date sendAt, 
		Date sentAt,
		Set<Target> targets
	) {
		this.uuid = uuid;
		this.message = message;
		this.status = status;
		this.sendAt = sendAt;
		this.sentAt = sentAt;
		this.targets = targets;
	}

	public String getUUID(){
		return this.uuid;
	}

	//@JsonProperty
	public String getContent() {
		return this.message;
	}

	//private void setMessage(String message) {
	//	this.message = message;
	//}

	//@JsonProperty
	public NotificationStatus getStatus() {
		return this.status;
	}

	//private void setStatus(NotificationStatus status) {
	//	this.status = status;
	//}

	//@JsonProperty
	public Date getSendAt() {
		return this.sendAt;
	}

	//private void setSendAt(Date sendAt) {
	//	this.sendAt = sendAt;
	//}

	//@JsonProperty
	public Date getSentAt() {
		return this.sentAt;
	}

	//private void setSentAt(Date sentAt) {
	//	this.sentAt = sentAt;
	//}
}
