package infrastructure;

import domain.EntitySQLFactory;
import domain.Target;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

/**
 * Responsible for retrieving and persisting {@link Target} entities.
 *
 * @author jonfreer
 */
public final class TargetRepository extends SQLRepository implements Repository<Target, UUID> {

  private final EntitySQLFactory<Target, UUID> targetFactory;
  private final Tracer tracer;

  /**
   * Constructs a instance of {@link TargetRepository}.
   *
   * @param unitOfWork The unit of work that this repository will contribute to.
   * @param targetFactory The factory that reconstitutes {@link Target} entities.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  public TargetRepository(
      SQLUnitOfWork unitOfWork, EntitySQLFactory<Target, UUID> targetFactory, Tracer tracer) {
    super(unitOfWork);

    this.targetFactory = targetFactory;
    this.tracer = tracer;
  }

  @Override
  public Set<Target> get(Query<Target> query) {
    return query.execute();
  }

  /** Retrieves the entity from the repository by a representation of the entity's identity. */
  @Override
  public Target get(UUID uuid) {
    Span span =
        this.tracer.buildSpan("TargetRepository#get").asChildOf(this.tracer.activeSpan()).start();
    Target target = null;
    final String targetSQL = "SELECT T.* FROM TARGET AS T WHERE T.UUID = ?;";

    try (Scope scope = this.tracer.scopeManager().activate(span, false);
        PreparedStatement getTargetStatement =
            this.getUnitOfWork().createPreparedStatement(targetSQL)) {

      getTargetStatement.setString(1, uuid.toString());
      try (ResultSet targetRs = getTargetStatement.executeQuery()) {
        if (targetRs.next()) {
          target = this.targetFactory.reconstitute(targetRs);
        }
      }
      return target;
    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }

  /**
   * Places the entity provided into the repository. If the entity provided already exists in the
   * repository, it's state is replaced with the state provided.
   */
  @Override
  public void put(Target target) {
    Span span =
        this.tracer.buildSpan("TargetRepository#put").asChildOf(this.tracer.activeSpan()).start();
    final String sql = "UPDATE TARGET SET NAME = ?, PHONE_NUMBER = ? WHERE UUID = ?;";

    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Target existingTarget = this.get(target.getId());
      if (existingTarget == null) {
        this.add(target);
      } else {

        // update the target.
        try (PreparedStatement updateTargetStatement =
            this.getUnitOfWork().createPreparedStatement(sql)) {
          updateTargetStatement.setString(1, target.getName());
          updateTargetStatement.setString(2, target.getPhoneNumber().toE164());
          updateTargetStatement.setString(3, target.getId().toString());
          updateTargetStatement.executeUpdate();
        } catch (SQLException x) {
          throw new RuntimeException(x);
        }
      }
    } finally {
      span.finish();
    }
  }

  /** Inserts the entity provided into the repository. */
  @Override
  public void add(Target target) {
    Span span =
        this.tracer.buildSpan("TargetRepository#add").asChildOf(this.tracer.activeSpan()).start();
    final String createTargetSQL =
        "INSERT INTO TARGET (UUID, NAME, PHONE_NUMBER) VALUES (?, ?, ?);";

    // create target.
    try (Scope scope = this.tracer.scopeManager().activate(span, false);
        PreparedStatement pStatement =
            this.getUnitOfWork().createPreparedStatement(createTargetSQL)) {
      pStatement.setString(1, target.getId().toString());
      pStatement.setString(2, target.getName());
      pStatement.setString(3, target.getPhoneNumber().toE164());
      pStatement.executeUpdate();
    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }

  /** Removes the entity from the repository with the representation of the identity provided. */
  @Override
  public void remove(UUID uuid) {
    Span span =
        this.tracer
            .buildSpan("TargetRepository#remove")
            .asChildOf(this.tracer.activeSpan())
            .start();
    final String deleteTargetSQL = "DELETE FROM TARGET WHERE UUID = ?;";
    final String deleteNotificationsAssociationSQL =
        "DELETE FROM NOTIFICATION_TARGET WHERE TARGET_UUID = ?;";
    try (Scope scope = this.tracer.scopeManager().activate(span, false);
        PreparedStatement deleteNotificationAssociationsStatement =
            this.getUnitOfWork().createPreparedStatement(deleteNotificationsAssociationSQL);
        PreparedStatement deleteTargetStatement =
            this.getUnitOfWork().createPreparedStatement(deleteTargetSQL)) {
      deleteNotificationAssociationsStatement.setString(1, uuid.toString());
      deleteNotificationAssociationsStatement.executeUpdate();

      deleteTargetStatement.setString(1, uuid.toString());
      deleteTargetStatement.executeUpdate();
    } catch (SQLException x) {
      throw new RuntimeException(x);
    } finally {
      span.finish();
    }
  }
}
