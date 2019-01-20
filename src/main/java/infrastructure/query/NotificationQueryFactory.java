package infrastructure.query;

import domain.Notification;
import infrastructure.DataMapper;
import infrastructure.NotificationDataMapper;
import infrastructure.UnitOfWork;
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
  public Query<Notification> createQuery(UnitOfWork unitOfWork) {
    DataMapper dm = unitOfWork.dataMappers().get(Notification.class);
    return new NotificationQuery((NotificationDataMapper) dm);
  }
}
