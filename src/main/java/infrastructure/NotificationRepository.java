package infrastructure;

import domain.EntitySQLFactory;
import domain.Notification;
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
  private final NotificationDataMapper notificationDataMapper;
  private final Tracer tracer;

  /**
   * Constructs a new {@link NotificationRepository}.
   *
   * @param unitOfWork The unit of work that this repository will contribute to.
   * @param notificationFactory The factory that reconstitutes {@link Notification} entities.
   * @param notificationDataMapper The data mapper that maps {@link Notification} entites to the
   *     database.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  public NotificationRepository(
      SQLUnitOfWork unitOfWork,
      EntitySQLFactory<Notification, UUID> notificationFactory,
      NotificationDataMapper notificationDataMapper,
      Tracer tracer) {
    super(unitOfWork);

    this.notificationFactory = notificationFactory;
    this.notificationDataMapper = notificationDataMapper;
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
    final Span span =
        this.tracer
            .buildSpan("NotificationRepostory#get(query)")
            .asChildOf(this.tracer.activeSpan())
            .start();
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
    final Span span =
        this.tracer
            .buildSpan("NotificationRepostory#get(uuid)")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return this.notificationDataMapper.find(uuid);
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
    final Span span =
        this.tracer
            .buildSpan("NotificationRepository#put")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      if (this.get(notification.getId()) == null) {
        this.add(notification);
      } else {
        this.notificationDataMapper.update(notification);
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
    final Span span =
        this.tracer
            .buildSpan("NotificationRepository#add")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      this.notificationDataMapper.insert(notification);
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
    final Span span =
        this.tracer
            .buildSpan("NotificationRepository#remove")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      this.notificationDataMapper.delete(uuid);
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
    final Span span =
        this.tracer
            .buildSpan("NotificationRepository#size")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return this.notificationDataMapper.count();
    } finally {
      span.finish();
    }
  }
}
