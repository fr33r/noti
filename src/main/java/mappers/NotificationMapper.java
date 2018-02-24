package mappers;

import java.util.HashSet;
import java.util.Set;

import domain.Audience;
import domain.Target;

public class NotificationMapper implements Mapper<domain.Notification, api.representations.Notification> {

	@Override
	public api.representations.Notification map(domain.Notification from) {
		Set<api.representations.Target> targets_sm = new HashSet<>();

		for(Target target : from.directRecipients()) {
			targets_sm.add(
				new api.representations.Target(
					target.getId(),
					target.getName(),
					target.getPhoneNumber().toE164()
				)
			);
		}

		Set<api.representations.Audience> audiences_sm = new HashSet<>();
		for(Audience audience : from.audiences()) {
			Set<api.representations.Target> members_sm = new HashSet<>();
			for(Target member : audience.members()) {
				members_sm.add(
					new api.representations.Target(
						member.getId(),
						member.getName(),
						member.getPhoneNumber().toE164()
					)
				);
			}
			audiences_sm.add(
				new api.representations.Audience(
					audience.getId(),
					audience.name(),
					members_sm
				)
			);
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
