package infrastructure;

import domain.Audience;
import domain.EntitySQLFactory;
import domain.Notification;
import domain.Target;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;

@Service
public class SQLRepositoryFactory extends RepositoryFactory {

  private final EntitySQLFactory<Notification, UUID> notificationFactory;
  private final EntitySQLFactory<Target, UUID> targetFactory;
  private final EntitySQLFactory<Audience, UUID> audienceFactory;
  private final Logger logger;
  private final Tracer tracer;

  @Inject
  public SQLRepositoryFactory(
      @Named("NotificationSQLFactory") EntitySQLFactory<Notification, UUID> notificationFactory,
      @Named("TargetSQLFactory") EntitySQLFactory<Target, UUID> targetFactory,
      @Named("AudienceSQLFactory") EntitySQLFactory<Audience, UUID> audienceFactory,
      Tracer tracer,
      Logger logger) {
    this.notificationFactory = notificationFactory;
    this.targetFactory = targetFactory;
    this.audienceFactory = audienceFactory;
    this.tracer = tracer;
    this.logger = logger;
  }

  @Override
  public Repository<Notification, UUID> createNotificationRepository(SQLUnitOfWork unitOfWork) {
    final Span span =
        this.tracer
            .buildSpan("SQLRepositoryFactory#createNotificationRepository")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return new NotificationRepository(
          unitOfWork,
          this.notificationFactory,
          new NotificationDataMapper(
              unitOfWork,
              this.notificationFactory,
              this.targetFactory,
              this.audienceFactory,
              this.logger),
          this.tracer); // both of these inherit from SQLRepository!
    } finally {
      span.finish();
    }
  }

  @Override
  public Repository<Target, UUID> createTargetRepository(SQLUnitOfWork unitOfWork) {
    final Span span =
        this.tracer
            .buildSpan("SQLRepositoryFactory#createTargetRepository")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return new TargetRepository(
          unitOfWork, this.targetFactory, this.tracer); // both of these inherit from SQLRepository!
    } finally {
      span.finish();
    }
  }

  @Override
  public Repository<Audience, UUID> createAudienceRepository(SQLUnitOfWork unitOfWork) {
    final Span span =
        this.tracer
            .buildSpan("SQLRepositoryFactory#createAudienceRepository")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return new AudienceRepository(
          unitOfWork,
          this.audienceFactory,
          new AudienceDataMapper(unitOfWork, this.audienceFactory, this.logger),
          this.tracer); // both of these inherit from SQL repository!
    } finally {
      span.finish();
    }
  }
}
