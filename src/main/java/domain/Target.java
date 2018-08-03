package domain;

import java.util.UUID;

public class Target extends Entity<UUID> implements Cloneable {

  private String name;
  private PhoneNumber phoneNumber;

  public Target(String name, PhoneNumber phoneNumber) {
    super();
    this.name = name;
    this.phoneNumber = phoneNumber;
  }

  public Target(UUID uuid, String name, PhoneNumber phoneNumber) {
    super(uuid);
    this.name = name;
    this.phoneNumber = phoneNumber;
  }

  public Target(Target target) {
    super(target.getId());
    this.name = target.getName();
    this.phoneNumber = target.getPhoneNumber();
  }

  @Override
  public boolean isAggregateRoot() {
    return true;
  }

  public PhoneNumber getPhoneNumber() {
    return this.phoneNumber;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public Object clone() {

    Target target = null;

    try {
      target = (Target) super.clone();
    } catch (CloneNotSupportedException ex) {
      // not possible;
    }

    return target;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder
        .append("[")
        .append("name=")
        .append(this.name)
        .append(", ")
        .append("phoneNumber=")
        .append(this.phoneNumber)
        .append(", ")
        .append("]");
    return builder.toString();
  }
}
