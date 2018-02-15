package domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Named;

@Named("NotificationFactory")
public class NotificationFactory {

	//i hate this. can't create mappers as they violate DDD. mappers are factories.
	public Notification createFrom(api.representations.Notification notification) {
		Notification noti = this.create(notification);
		Set<Message> messages = this.createMessages(noti);
		noti.messages(messages);
		return noti;
	}
	
	public Notification createFrom(api.representations.Notification notification, Set<Message> messages) {
		Notification noti = this.create(notification);
		noti.messages(messages);
		return noti;
	}
	
	private Set<Message> createMessages(Notification notification){
		Set<Message> messages = new HashSet<>();
		final PhoneNumber from = new PhoneNumber("1", "614", "412", "5540");
		int sequenceNum = 0;

		for(Target target : notification.directRecipients()) {
			messages.add(
				new Message(
					sequenceNum,
					from, 
					target.getPhoneNumber(), 
					notification.content(), 
					MessageStatus.PENDING, 
					""
				)
			);
			sequenceNum++;
		}
		
		return messages;
	}
	
	private Notification create(api.representations.Notification notification) {
		Set<Target> targets = new HashSet<>();
		for(api.representations.Target target : notification.getTargets()) {
			Set<Tag> tags = new HashSet<>();
			for(api.representations.Tag tag : target.getTags()) {
				tags.add(new Tag(tag.getName()));
			}
			targets.add(new Target(target.getUUID(), target.getName(), new PhoneNumber(target.getPhoneNumber()), tags));
		}
		
		NotificationBuilder builder = new NotificationBuilder();
		UUID uuid = notification.getUUID() == null ? UUID.randomUUID() : UUID.fromString(notification.getUUID());
		
		Notification domainNotification = 
			builder
			.identity(uuid)
			.content(notification.getContent())
			.targets(targets)
			.sendAt(notification.getSendAt())
			.sentAt(notification.getSentAt())
			.build();
		
		//compare the persisted state with the state computed:
		NotificationStatus desiredStatus = 
			NotificationStatus.valueOf(notification.getStatus().toString());
		if(domainNotification.status() != desiredStatus) {
			//throw.
		}
		
		return domainNotification;
	}
}
