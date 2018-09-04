package infrastructure;

import domain.AudienceSQLFactory;
import domain.Notification;
import domain.NotificationSQLFactory;
import domain.TargetSQLFactory;
import io.opentracing.Tracer;
import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;

@Service
public final class NotificationQueryFactory extends QueryFactory<Notification> {

  private final Tracer tracer;
  private final Logger logger;

  @Inject
  public NotificationQueryFactory(Tracer tracer, Logger logger) {
    this.tracer = tracer;
    this.logger = logger;
  }

  @Override
  public Query<Notification> createQuery(SQLUnitOfWork unitOfWork) {
    NotificationDataMapper dataMapper =
        new NotificationDataMapper(
            unitOfWork,
            new NotificationSQLFactory(this.tracer),
            new TargetSQLFactory(this.tracer),
            new AudienceSQLFactory(this.tracer),
            this.logger);

    return new NotificationQuery(dataMapper);
  }
}
