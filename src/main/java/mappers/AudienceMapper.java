package mappers;

import domain.Audience;
import domain.Target;

import java.util.HashSet;
import java.util.Set;

public class AudienceMapper implements Mapper<Audience, api.representations.Audience> {

	@Override
	public api.representations.Audience map(Audience from) {
		Set<api.representations.Target> members = new HashSet<>();
		for(Target member : from.members()) {
			members.add(
				new api.representations.Target(
					member.getId(),
					member.getName(),
					member.getPhoneNumber().toE164()
				)
			);
		}
		return new api.representations.Audience(
			from.getId(),
			from.name(),
			members
		);
	}
}
