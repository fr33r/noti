package domain;

import java.util.UUID;

// temporary solution for now. need to look more into
// factories with DDD. not a fan of everything being a concrete type.
public class TargetFactory {

  public Target createFrom(application.Target target) {
    UUID uuid = target.getUUID() == null ? UUID.randomUUID() : target.getUUID();
    return new Target(uuid, target.getName(), new PhoneNumber(target.getPhoneNumber()));
  }
}
