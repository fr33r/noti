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
import org.slf4j.LoggerFactory;

@Service
public class SQLRepositoryFactory extends RepositoryFactory {

  private final EntitySQLFactory<Notification, UUID> notificationFactory;
  private final EntitySQLFactory<Target, UUID> targetFactory;
  private final EntitySQLFactory<Audience, UUID> audienceFactory;
  private final Tracer tracer;

  @Inject
  public SQLRepositoryFactory(
      @Named("NotificationSQLFactory") EntitySQLFactory<Notification, UUID> notificationFactory,
      @Named("TargetSQLFactory") EntitySQLFactory<Target, UUID> targetFactory,
      @Named("AudienceSQLFactory") EntitySQLFactory<Audience, UUID> audienceFactory,
      Tracer tracer) {
    this.notificationFactory = notificationFactory;
    this.targetFactory = targetFactory;
    this.audienceFactory = audienceFactory;
    this.tracer = tracer;
  }

  @Override
  public Repository<Notification, UUID> createNotificationRepository(SQLUnitOfWork unitOfWork) {
    String className = SQLRepositoryFactory.class.getName();
    String spanName = String.format("%s#createNotificationRepository", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return new NotificationRepository(
          unitOfWork,
          this.notificationFactory,
          new NotificationDataMapper(
              unitOfWork,
              this.notificationFactory,
              this.targetFactory,
              this.audienceFactory,
              LoggerFactory.getLogger(NotificationDataMapper.class)),
          this.tracer);
    } finally {
      span.finish();
    }
  }

  @Override
  public Repository<Target, UUID> createTargetRepository(SQLUnitOfWork unitOfWork) {
    String className = SQLRepositoryFactory.class.getName();
    String spanName = String.format("%s#createTargetRepository", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return new TargetRepository(
          unitOfWork,
          this.targetFactory,
          new TargetDataMapper(
              unitOfWork, this.targetFactory, LoggerFactory.getLogger(TargetDataMapper.class)),
          this.tracer);
    } finally {
      span.finish();
    }
  }

  @Override
  public Repository<Audience, UUID> createAudienceRepository(SQLUnitOfWork unitOfWork) {
    String className = SQLRepositoryFactory.class.getName();
    String spanName = String.format("%s#createAudienceRepository", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return new AudienceRepository(
          unitOfWork,
          this.audienceFactory,
          new AudienceDataMapper(
              unitOfWork, this.audienceFactory, LoggerFactory.getLogger(AudienceDataMapper.class)),
          this.tracer);
    } finally {
      span.finish();
    }
  }
}
