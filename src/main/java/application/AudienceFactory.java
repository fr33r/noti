package application;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

public class AudienceFactory {

  private final TargetFactory targetFactory;

  @Inject
  public AudienceFactory(TargetFactory targetFactory) {
    this.targetFactory = targetFactory;
  }

  public Audience createFrom(api.representations.json.Audience audience) {
    Set<Target> targets = new HashSet<>();
    for (api.representations.json.Target target : audience.getMembers()) {
      targets.add(this.targetFactory.createFrom(target));
    }
    return new Audience(audience.getUUID(), audience.getName(), targets);
  }

  public Audience createFrom(api.representations.xml.Audience audience) {
    Set<Target> targets = new HashSet<>();
    for (api.representations.xml.Target target : audience.getMembers()) {
      targets.add(this.targetFactory.createFrom(target));
    }
    return new Audience(audience.getUUID(), audience.getName(), targets);
  }

  public Audience createFrom(domain.Audience audience) {
    Set<Target> members = new HashSet<>();
    for (domain.Target member : audience.members()) {
      members.add(this.targetFactory.createFrom(member));
    }
    return new Audience(audience.getId(), audience.name(), members);
  }
}
