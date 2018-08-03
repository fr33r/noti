package application;

public class TargetFactory {

  public Target createFrom(api.representations.xml.Target target) {
    return new Target(target.getUUID(), target.getName(), target.getPhoneNumber());
  }

  public Target createFrom(api.representations.json.Target target) {
    return new Target(target.getUUID(), target.getName(), target.getPhoneNumber());
  }

  public Target createFrom(domain.Target target) {
    return new Target(target.getId(), target.getName(), target.getPhoneNumber().toE164());
  }
}
