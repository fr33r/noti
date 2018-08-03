package domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// temporary solution for now. need to look more into
// factories with DDD. not a fan of everything being a concrete type.
//
// essentially this factory is responsible for creating a domain entity
// (Audience) for the first time; its beginning of its lifecyce.
//
// other factories are responsible for its reconstitution.
public class AudienceFactory {

  // what happens here if not all of the target info is provided?
  // perhaps just require it to be honest...
  public Audience createFrom(application.Audience audience) {
    UUID uuid = audience.getUUID() == null ? UUID.randomUUID() : audience.getUUID();
    Set<Target> members = new HashSet<>();
    for (application.Target target : audience.getMembers()) {
      members.add(
          new Target(target.getUUID(), target.getName(), new PhoneNumber(target.getPhoneNumber())));
    }
    return new Audience(uuid, audience.getName(), members);
  }
}
