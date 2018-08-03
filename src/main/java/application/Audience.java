package application;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Defines the general representation of an Audience resource. This representation can easily be
 * deserialized and serialized for the 'application/json' and 'application/xml' media types.
 *
 * @author Jon Freer
 */
public class Audience {

  private final UUID uuid;
  private final String name;
  private final Set<Target> members;

  /** Constructs an empty instance of {@link Audience}. */
  public Audience() {
    this.uuid = null;
    this.name = null;
    this.members = new HashSet<>();
  }

  /**
   * Constructs a fully initialized instance of {@link Audience}.
   *
   * @param uuid The universally unique identifier for this audience.
   * @param name The name of the audience.
   * @param members The members that collectively resemble this audience.
   */
  public Audience(UUID uuid, String name, Set<Target> members) {
    this.uuid = uuid;
    this.name = name;
    this.members = members;
  }

  /**
   * Retrieves the universally unique identifier for this audience.
   *
   * @return The universally unique identifier for this audience.
   */
  public UUID getUUID() {
    return this.uuid;
  }

  /**
   * Retrieves the name of the audience.
   *
   * @return The name of the audience.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Retrieves the members that collectively resemble this audience.
   *
   * @return The members that collectively resemble this audience.
   */
  public Set<Target> getMembers() {
    return this.members;
  }

  /**
   * Determines if the provided instance is equal to the calling instance.
   *
   * @param obj The instance to compare to the calling instance for equality.
   * @return {@code true} if the two instances are equal; {@code false} otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass()) return false;

    Audience audience = (Audience) obj;
    boolean nameIsEqual =
        (this.name == null && audience.name == null)
            || (this.name != null && audience.name != null && this.name.equals(audience.name));
    boolean membersAreEqual = this.members.equals(audience.members);

    return nameIsEqual && membersAreEqual;
  }

  /**
   * Generates hash code for this instance.
   *
   * @return The hash code represented as an integer.
   */
  @Override
  public int hashCode() {
    int hashCode = 1;
    final int prime = 17;

    if (this.name != null) {
      hashCode = hashCode * prime + this.name.hashCode();
    }

    if (this.members != null) {
      hashCode = hashCode * prime + this.members.hashCode();
    }

    return hashCode;
  }
}
