package mappers;

import java.util.HashSet;
import java.util.Set;

import domain.Tag;
import domain.Target;

public class NotificationMapper implements Mapper<domain.Notification, api.representations.Notification> {

	@Override
	public api.representations.Notification map(domain.Notification from) {
		Set<api.representations.Target> targets_sm = new HashSet<>();
		for(Target target : from.getTargets()) {
			Set<api.representations.Tag> tags_sm = new HashSet<>();
			for(Tag tag : target.getTags()) {
				tags_sm.add(new api.representations.Tag(tag.getName()));
			}
			targets_sm.add(new api.representations.Target(target.getId(), target.getName(), target.getPhoneNumber().toE164(), tags_sm));
		}
		
		api.representations.Notification noti_sm = 
			new api.representations.Notification(
				from.getId().toString(),
				from.getContent(),
				api.representations.NotificationStatus.valueOf(
					from.getStatus().toString()
				),
				from.getSendAt(),
				from.getSentAt(),
				targets_sm
			);
		return noti_sm;		
	}
}
