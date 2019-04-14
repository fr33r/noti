package infrastructure;

import domain.Audience;
import domain.EntitySQLFactory;
import domain.Notification;
import domain.Target;
import domain.Template;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import org.jvnet.hk2.annotations.Service;

@Service
public class SQLRepositoryFactory extends RepositoryFactory {

  // TODO - Rename these to ReconstituationFactory.
  private final EntitySQLFactory<Notification, UUID> notificationFactory;
  private final EntitySQLFactory<Target, UUID> targetFactory;
  private final EntitySQLFactory<Audience, UUID> audienceFactory;
  private final EntitySQLFactory<Template, UUID> templateFactory;
  private final Tracer tracer;

  @Inject
  public SQLRepositoryFactory(
      @Named("NotificationSQLFactory") EntitySQLFactory<Notification, UUID> notificationFactory,
      @Named("TargetSQLFactory") EntitySQLFactory<Target, UUID> targetFactory,
      @Named("AudienceSQLFactory") EntitySQLFactory<Audience, UUID> audienceFactory,
      @Named("TemplateSQLFactory") EntitySQLFactory<Template, UUID> templateFactory,
      Tracer tracer) {
    this.notificationFactory = notificationFactory;
    this.targetFactory = targetFactory;
    this.audienceFactory = audienceFactory;
    this.templateFactory = templateFactory;
    this.tracer = tracer;
  }

  @Override
  public Repository<Notification, UUID> createNotificationRepository(UnitOfWork unitOfWork) {
    String className = SQLRepositoryFactory.class.getName();
    String spanName = String.format("%s#createNotificationRepository", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return new NotificationRepository(unitOfWork, this.notificationFactory, this.tracer);
    } finally {
      span.finish();
    }
  }

  @Override
  public Repository<Target, UUID> createTargetRepository(UnitOfWork unitOfWork) {
    String className = SQLRepositoryFactory.class.getName();
    String spanName = String.format("%s#createTargetRepository", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return new TargetRepository(unitOfWork, this.targetFactory, this.tracer);
    } finally {
      span.finish();
    }
  }

  @Override
  public Repository<Audience, UUID> createAudienceRepository(UnitOfWork unitOfWork) {
    String className = SQLRepositoryFactory.class.getName();
    String spanName = String.format("%s#createAudienceRepository", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return new AudienceRepository(unitOfWork, this.audienceFactory, this.tracer);
    } finally {
      span.finish();
    }
  }

  @Override
  public Repository<Template, UUID> createTemplateRepository(UnitOfWork unitOfWork) {
    String className = SQLRepositoryFactory.class.getName();
    String spanName = String.format("%s#createTemplateRepository", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return new TemplateRepository(unitOfWork, this.templateFactory, this.tracer);
    } finally {
      span.finish();
    }
  }
}
