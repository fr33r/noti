package domain;

import java.util.Set;
import java.util.UUID;

public class Audience extends Entity<UUID> {

  private String name;
  private Set<Target> members;

  public Audience(UUID uuid, String name, Set<Target> members) {
    super(uuid);
    this.name = name;
    this.members = members;
  }

  public Audience(Audience toCopy) {
    super(toCopy.getId());
    this.name = toCopy.name();
    this.members = toCopy.members();
  }

  public String name() {
    return this.name;
  }

  public void named(String name) {
    this.name = name;
  }

  public Set<Target> members() {
    return this.members;
  }

  public void include(Target member) {
    this.members.add(member);
  }

  public void remove(Target member) {
    this.members.remove(member);
  }

  @Override
  public boolean isAggregateRoot() {
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Audience [name=");
    builder.append(this.name);
    builder.append(", members=");
    builder.append(this.members);
    builder.append("]");
    return builder.toString();
  }
}
