package api.representations.siren;

import api.representations.Representation;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import siren.Action;
import siren.Entity;
import siren.EntityBase;
import siren.Link;

// this class uses object adapter pattern.
// mapping between Entity and Representation interfaces.

/**
 * Defines the {@code application/vnd.siren+json} representation of all resources.
 *
 * @author Jon Freer
 */
public final class SirenEntityRepresentation extends Representation {

  private final Entity sirenEntity;

  /** Constructs a new {@link SirenEntityRepresentation}. */
  private SirenEntityRepresentation() {
    super(new MediaType("application", "vnd.siren+json"));
    this.sirenEntity = null;
  }

  /**
   * Constructs a new {@link SirenEntityRepresentation} based on the provided Siren entity.
   *
   * @param sirenEntity
   */
  private SirenEntityRepresentation(Entity sirenEntity) {
    super(new MediaType("application", "vnd.siren+json"));
    this.sirenEntity = sirenEntity;
  }

  /**
   * A builder of {@link SirenEntityRepresentation} instances.
   *
   * @author Jon Freer
   */
  public static final class Builder extends Representation.Builder {

    private Entity entity;

    /** Constructs a builder of {@link SirenEntityRepresentation} instances. */
    public Builder() {
      super(new MediaType("application", "vnd.siren+json"));
    }

    /**
     * Sets the Siren entity to base the {@link SirenEntityRepresentation} being being built off of.
     *
     * @param entity The desired Siren entity to base the {@link SirenEntityRepresentation} being
     *     built off of.
     * @return The updated {@link SirenEntityRepresentation} builder.
     */
    public Builder entity(Entity entity) {
      this.entity = entity;
      return this;
    }

    /**
     * Builds the {@link SirenEntityRepresentation} instance.
     *
     * @return The {@link SirenEntityRepresentation} instance.
     */
    @Override
    public Representation build() {
      return new SirenEntityRepresentation(this.entity);
    }
  }

  /**
   * Retrieves the classes of the Siren entity representation.
   *
   * @return The classes of the Siren entity representation.
   */
  @JsonProperty("class")
  public List<String> getKlass() {
    return this.sirenEntity.getKlass();
  }

  /**
   * Retrieves the title of the Siren entity representation.
   *
   * @return The title of the Siren entity representation.
   */
  public String getTitle() {
    return this.sirenEntity.getTitle();
  }

  /**
   * Retrieves the properties of the Siren entity representation.
   *
   * @return The properties of the Siren entity representation.
   */
  public Map<String, Object> getProperties() {
    return this.sirenEntity.getProperties();
  }

  /**
   * Retrieves the actions of the Siren entity representation.
   *
   * @return The actions of the Siren entity representation.
   */
  public List<Action> getActions() {
    return this.sirenEntity.getActions();
  }

  /**
   * Retrieves the links of the Siren entity representation.
   *
   * @return The links of the Siren entity representation.
   */
  public List<Link> getLinks() {
    return this.sirenEntity.getLinks();
  }

  /**
   * Retrieves the sub-entities of the Sirent entity representation.
   *
   * @return The sub-entities of the Siren entity representation.
   */
  public List<EntityBase> getEntities() {
    return this.sirenEntity.getEntities();
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != this.getClass()) return false;
    SirenEntityRepresentation representation = (SirenEntityRepresentation) obj;
    return this.sirenEntity.equals(representation.sirenEntity);
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    final int prime = 17;
    int hashCode = 1;

    if (this.sirenEntity != null) {
      hashCode = hashCode * prime + this.sirenEntity.hashCode();
    }

    return hashCode;
  }
}
