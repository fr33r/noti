package mappers;

import java.util.HashSet;
import java.util.Set;

import domain.Audience;
import domain.Tag;
import domain.Target;

public class NotificationMapper implements Mapper<domain.Notification, api.representations.Notification> {

	@Override
	public api.representations.Notification map(domain.Notification from) {
		Set<api.representations.Target> targets_sm = new HashSet<>();

		for(Target target : from.directRecipients()) {
			Set<api.representations.Tag> tags_sm = new HashSet<>();
			for(Tag tag : target.getTags()) {
				tags_sm.add(new api.representations.Tag(tag.getName()));
			}
			targets_sm.add(new api.representations.Target(target.getId(), target.getName(), target.getPhoneNumber().toE164(), tags_sm));
		}

		Set<api.representations.Audience> audiences_sm = new HashSet<>();
		for(Audience audience : from.audiences()) {
			Set<api.representations.Target> members_sm = new HashSet<>();
			for(Target member : audience.members()) {
				members_sm.add(new api.representations.Target(member.getId(), member.getName(), member.getPhoneNumber().toE164(), null));
			}
			audiences_sm.add(new api.representations.Audience(audience.getId(), audience.name(), members_sm));
		}

		api.representations.Notification noti_sm = 
			new api.representations.Notification(
				from.getId(),
				from.content(),
				api.representations.NotificationStatus.valueOf(
					from.status().toString()
				),
				targets_sm,
				audiences_sm,
				from.sendAt(),
				from.sentAt()
			);
		return noti_sm;
	}
}
