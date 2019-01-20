package infrastructure;

import domain.EntitySQLFactory;
import domain.Notification;
import infrastructure.query.Query;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a {@link Notification} collection.
 *
 * @author jonfreer
 */
public final class NotificationRepository extends SQLRepository
    implements Repository<Notification, UUID> {

  private final EntitySQLFactory<Notification, UUID> notificationFactory;
  private final Tracer tracer;

  /**
   * Constructs a new {@link NotificationRepository}.
   *
   * @param unitOfWork The unit of work that this repository will contribute to.
   * @param notificationFactory The factory that reconstitutes {@link Notification} entities.
   *     database.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  public NotificationRepository(
      UnitOfWork unitOfWork,
      EntitySQLFactory<Notification, UUID> notificationFactory,
      Tracer tracer) {
    super(unitOfWork);

    this.notificationFactory = notificationFactory;
    this.tracer = tracer;
  }

  /**
   * Retrieves the notifications matching the provided {@link Query}.
   *
   * @param query The {@link Query} to match against.
   * @return The collection of notifications matching the provided {@link Query}.
   */
  @Override
  public Set<Notification> get(final Query<Notification> query) {
    final String className = NotificationRepository.class.getName();
    final String spanName = String.format("%s#get(query)", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return query.execute();
    } finally {
      span.finish();
    }
  }

  /**
   * Retrieves the {@link Notification} identified by the universally unique identifier provided.
   *
   * @param uuid The universally unique identifier of the {@link Notification} to retrieve.
   * @return The {@link Notification} with the universally unique identifier provided.
   */
  @Override
  public Notification get(final UUID uuid) {
    final String className = NotificationRepository.class.getName();
    final String spanName = String.format("%s#get(uuid)", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      DataMapper dm = this.getUnitOfWork().dataMappers().get(Notification.class);
      return (Notification) dm.find(uuid);
    } finally {
      span.finish();
    }
  }

  /**
   * Places the {@link Notification} provided into the repository. In the event the {@link
   * Notification} provided already exists in the repository, the prexisting one will be replaced
   * with the one provided.
   *
   * @param notification The notification to put into the repository.
   */
  @Override
  public void put(final Notification notification) {
    final String className = NotificationRepository.class.getName();
    final String spanName = String.format("%s#put", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      if (this.get(notification.getId()) == null) {
        this.add(notification);
      } else {
        this.getUnitOfWork().alter(notification);
      }
    } finally {
      span.finish();
    }
  }

  /**
   * Adds the provided {@link Notification} into the repository.
   *
   * @param notification The notification to add into the repository.
   */
  @Override
  public void add(final Notification notification) {
    final String className = NotificationRepository.class.getName();
    final String spanName = String.format("%s#add", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      this.getUnitOfWork().add(notification);
    } finally {
      span.finish();
    }
  }

  /**
   * Removes the {@link Notification} with the universally unique identifier provided.
   *
   * @param uuid The universally unique identifier of the {@link Notification} to retrieve.
   */
  @Override
  public void remove(final UUID uuid) {
    final String className = NotificationRepository.class.getName();
    final String spanName = String.format("%s#remove", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Notification notification = this.get(uuid);
      if (notification != null) {
        this.getUnitOfWork().remove(notification);
      }
    } finally {
      span.finish();
    }
  }

  /**
   * Retrieves the number of notifications within the repository.
   *
   * @return The number of notifications within the repository.
   */
  @Override
  public int size() {
    final String className = NotificationRepository.class.getName();
    final String spanName = String.format("%s#size", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      DataMapper dm = this.getUnitOfWork().dataMappers().get(Notification.class);
      return dm.count();
    } finally {
      span.finish();
    }
  }
}
