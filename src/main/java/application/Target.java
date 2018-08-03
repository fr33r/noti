package application;

import java.util.UUID;

public class Target {

  private final UUID uuid;
  private final String name;
  private final String phoneNumber;

  public Target() {
    this.uuid = null;
    this.name = null;
    this.phoneNumber = null;
  }

  public Target(final String name, final String phoneNumber) {
    this.uuid = null;
    this.name = name;
    this.phoneNumber = phoneNumber;
  }

  public Target(final UUID uuid, final String name, final String phoneNumber) {
    this.uuid = uuid;
    this.name = name;
    this.phoneNumber = phoneNumber;
  }

  public UUID getUUID() {
    return uuid;
  }

  public String getName() {
    return name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }
}
