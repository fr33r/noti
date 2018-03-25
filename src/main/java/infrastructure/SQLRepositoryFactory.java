package infrastructure;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

import domain.Audience;
import domain.EntitySQLFactory;
import domain.Notification;
import domain.Target;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

@Service
public class SQLRepositoryFactory extends RepositoryFactory {

	private EntitySQLFactory<Notification, UUID> notificationFactory;
	private EntitySQLFactory<Target, UUID> targetFactory;
	private EntitySQLFactory<Audience, UUID> audienceFactory;
	private final Tracer tracer;

	@Inject
	public SQLRepositoryFactory(
		@Named("NotificationSQLFactory") EntitySQLFactory<Notification, UUID> notificationFactory,
		@Named("TargetSQLFactory") EntitySQLFactory<Target, UUID> targetFactory,
		@Named("AudienceSQLFactory") EntitySQLFactory<Audience, UUID> audienceFactory,
		Tracer tracer
	) {
		this.notificationFactory = notificationFactory;
		this.targetFactory = targetFactory;
		this.audienceFactory = audienceFactory;
		this.tracer = tracer;
	}

	@Override
	public Repository<Notification, UUID> createNotificationRepository(SQLUnitOfWork unitOfWork) {
		final Span span =
			this.tracer
				.buildSpan("SQLRepositoryFactory#createNotificationRepository")
				.asChildOf(this.tracer.activeSpan())
				.start();
		try(final Scope scope = this.tracer.scopeManager().activate(span, false)) {
			return new NotificationRepository(unitOfWork, this.notificationFactory, this.tracer); //both of these inherit from SQLRepository!
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
		try(final Scope scope = this.tracer.scopeManager().activate(span, false)) {
			return new TargetRepository(unitOfWork, this.targetFactory, this.tracer); //both of these inherit from SQLRepository!
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
		try(final Scope scope = this.tracer.scopeManager().activate(span, false)) {
			return new AudienceRepository(unitOfWork, this.audienceFactory, this.tracer); //both of these inherit from SQL repository!
		} finally {
			span.finish();
		}
	}
}
