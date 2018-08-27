package infrastructure;

import domain.Audience;
import domain.EntitySQLFactory;
import domain.Target;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Response for retrieving and persisting the aggregate rooted by the {@link Audience} entity.
 *
 * @author Jon Freer
 */
public final class AudienceRepository extends SQLRepository implements Repository<Audience, UUID> {

  private final EntitySQLFactory<Audience, UUID> audienceFactory;
  private final Tracer tracer;

  /**
   * Constructs an instance of {@link AudienceRepository}.
   *
   * @param unitOfWork The unit of work that this repository will contribue to.
   * @param audienceFactory The factory that reconstitutes the aggregate rooted by the {@link
   *     Audience} entity.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  public AudienceRepository(
      SQLUnitOfWork unitOfWork, EntitySQLFactory<Audience, UUID> audienceFactory, Tracer tracer) {
    super(unitOfWork);
    // if(audience is not an aggregate root) throw;
    this.audienceFactory = audienceFactory;
    this.tracer = tracer;
  }

  @Override
  public Set<Audience> get(Query<Audience> query) {
    return query.execute();
  }

  /**
   * Retrieves the aggregate from the repository by utilizing the representation of the aggregate
   * root's identity.
   *
   * @param uuid The representation of the aggregate root's identity.
   */
  @Override
  public Audience get(final UUID uuid) {
    final Span span =
        this.tracer.buildSpan("AudienceRepository#get").asChildOf(this.tracer.activeSpan()).start();
    Audience audience = null;
    final String audienceSQL = "SELECT A.* FROM AUDIENCE AS A WHERE A.UUID = ?;";
    final String membersSQL =
        "SELECT T.* FROM TARGET AS T INNER JOIN AUDIENCE_TARGET AS AT ON T.UUID = AT.TARGET_UUID WHERE AT.AUDIENCE_UUID = ?";

    try (final Scope scope = this.tracer.scopeManager().activate(span, false);
        final PreparedStatement getAudienceStatement =
            this.getUnitOfWork().createPreparedStatement(audienceSQL);
        final PreparedStatement getAudienceMembersStatement =
            this.getUnitOfWork().createPreparedStatement(membersSQL)) {
      getAudienceStatement.setString(1, uuid.toString());
      getAudienceMembersStatement.setString(1, uuid.toString());

      try (final ResultSet audienceRS = getAudienceStatement.executeQuery();
          final ResultSet membersRs = getAudienceMembersStatement.executeQuery()) {
        if (audienceRS.next()) {
          audience = this.audienceFactory.reconstitute(audienceRS, membersRs);
        }
      }
      return audience;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }

  /**
   * Places the aggregate into the repository. If the aggregate already exists in the repository,
   * it's state is replaced with the state provided.
   */
  @Override
  public void put(final Audience audience) {
    final Span span =
        this.tracer.buildSpan("AudienceRepository#put").asChildOf(this.tracer.activeSpan()).start();
    final String audienceSQL = "UPDATE AUDIENCE SET NAME = ? WHERE UUID = ?;";
    final String associateMemberSQL =
        "INSERT INTO AUDIENCE_TARGET (AUDIENCE_UUID, TARGET_UUID) VALUES (?,  ?);";
    final String disassociateMemberSQL =
        "DELETE FROM AUDIENCE_TARGET WHERE AUDIENCE_UUID = ? AND TARGET_UUID = ?;";

    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Audience existingAudience = this.get(audience.getId());
      if (existingAudience == null) {
        this.add(audience);
      } else {
        try (final PreparedStatement updateAudienceStatement =
            this.getUnitOfWork().createPreparedStatement(audienceSQL); ) {
          updateAudienceStatement.setString(1, audience.name());
          updateAudienceStatement.setString(2, audience.getId().toString());
          updateAudienceStatement.executeUpdate();

          Set<UUID> toAssociate = new HashSet<>();
          Set<UUID> toDisassociate = new HashSet<>();

          // determine which members are being added.
          for (Target member : audience.members()) {
            boolean exists = false;
            for (Target existingMember : existingAudience.members()) {
              if (member.getId().equals(existingMember.getId())) {
                exists = true;
                break;
              }
            }
            if (!exists) {
              toAssociate.add(member.getId());
            }
          }

          // determine which members are being removed.
          for (Target existingMember : existingAudience.members()) {
            boolean exists = false;
            for (Target member : audience.members()) {
              if (existingMember.getId().equals(member.getId())) {
                exists = true;
                break;
              }
            }
            if (!exists) {
              toDisassociate.add(existingMember.getId());
            }
          }

          for (UUID uuid : toAssociate) {
            try (final PreparedStatement associateMemberStatement =
                this.getUnitOfWork().createPreparedStatement(associateMemberSQL)) {
              associateMemberStatement.setString(1, audience.getId().toString());
              associateMemberStatement.setString(2, uuid.toString());
              associateMemberStatement.executeUpdate();
            }
          }

          for (UUID uuid : toDisassociate) {
            try (final PreparedStatement disassociateMemberStatement =
                this.getUnitOfWork().createPreparedStatement(disassociateMemberSQL)) {
              disassociateMemberStatement.setString(1, audience.getId().toString());
              disassociateMemberStatement.setString(2, uuid.toString());
              disassociateMemberStatement.executeUpdate();
            }
          }
        } catch (SQLException x) {
          throw new RuntimeException(x);
        }
      }
    } finally {
      span.finish();
    }
  }

  /**
   * Inserts the aggregate root provided into the repository.
   *
   * @param audience The aggregate to be inserted into the repository.
   */
  @Override
  public void add(final Audience audience) {
    final Span span =
        this.tracer.buildSpan("AudienceRepository#add").asChildOf(this.tracer.activeSpan()).start();
    final String audienceSQL = "INSERT INTO AUDIENCE (UUID, NAME) VALUES (?, ?);";
    final String associateMemberSQL =
        "INSERT INTO AUDIENCE_TARGET (AUDIENCE_UUID, TARGET_UUID) VALUES (?, ?);";

    try (final Scope scope = this.tracer.scopeManager().activate(span, false);
        final PreparedStatement createAudienceStatement =
            this.getUnitOfWork().createPreparedStatement(audienceSQL)) {
      createAudienceStatement.setString(1, audience.getId().toString());
      createAudienceStatement.setString(2, audience.name());
      createAudienceStatement.executeUpdate();

      for (Target member : audience.members()) {
        try (PreparedStatement associateMemberStatement =
            this.getUnitOfWork().createPreparedStatement(associateMemberSQL)) {
          associateMemberStatement.setString(1, audience.getId().toString());
          associateMemberStatement.setString(2, member.getId().toString());
          associateMemberStatement.executeUpdate();
        }
      }
    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }

  /**
   * Removes the aggregate from the repository with the aggregate root with the representation of
   * the identify provided.
   *
   * @param uuid The representation of the identity of the aggregate root of the aggregate to be
   *     removed.
   */
  @Override
  public void remove(final UUID uuid) {
    final Span span =
        this.tracer.buildSpan("AudienceRepository#add").asChildOf(this.tracer.activeSpan()).start();
    final String disassociateMembersSQL = "DELETE FROM AUDIENCE_TARGET WHERE AUDIENCE_UUID = ?;";
    final String audienceSQL = "DELETE FROM AUDIENCE WHERE UUID = ?;";
    try (final Scope scope = this.tracer.scopeManager().activate(span, false);
        final PreparedStatement removeAudienceStatement =
            this.getUnitOfWork().createPreparedStatement(audienceSQL);
        final PreparedStatement disassociateMembersStatement =
            this.getUnitOfWork().createPreparedStatement(disassociateMembersSQL)) {
      disassociateMembersStatement.setString(1, uuid.toString());
      disassociateMembersStatement.executeUpdate();

      removeAudienceStatement.setString(1, uuid.toString());
      removeAudienceStatement.executeUpdate();
    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }
}
