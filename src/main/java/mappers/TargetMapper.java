package mappers;

public final class TargetMapper implements Mapper<domain.Target, api.representations.Target> {

	@Override
	public api.representations.Target map(domain.Target from) {
		return new api.representations.Target(from.getId(), from.getName(), from.getPhoneNumber().toE164());
	}
}
