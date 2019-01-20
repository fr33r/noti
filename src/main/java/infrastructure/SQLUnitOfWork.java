package infrastructure;

import domain.Audience;
import domain.Entity;
import domain.EntitySQLFactory;
import domain.Notification;
import domain.Target;
import io.opentracing.Tracer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.LoggerFactory;

public class SQLUnitOfWork extends UnitOfWork {

  private final EntitySQLFactory<Notification, UUID> notificationFactory;
  private final EntitySQLFactory<Target, UUID> targetFactory;
  private final EntitySQLFactory<Audience, UUID> audienceFactory;
  private final ConnectionFactory connectionFactory;
  private final Tracer tracer;
  private final Map<Class, DataMapper> dataMappers;
  private final Connection connection;

  public SQLUnitOfWork(
      ConnectionFactory connectionFactory,
      EntitySQLFactory<Notification, UUID> notificationFactory,
      EntitySQLFactory<Target, UUID> targetFactory,
      EntitySQLFactory<Audience, UUID> audienceFactory,
      Tracer tracer) {
    super();
    this.connectionFactory = connectionFactory;
    this.notificationFactory = notificationFactory;
    this.targetFactory = targetFactory;
    this.audienceFactory = audienceFactory;
    this.tracer = tracer;
    this.dataMappers = new HashMap<>();
    this.connection = this.connectionFactory.createConnection();

    DataMapper ndm =
        new NotificationDataMapper(
            connection,
            this.notificationFactory,
            this.targetFactory,
            this.audienceFactory,
            LoggerFactory.getLogger(NotificationDataMapper.class));
    DataMapper tdm =
        new TargetDataMapper(
            this.connection, this.targetFactory, LoggerFactory.getLogger(TargetDataMapper.class));
    DataMapper adm =
        new AudienceDataMapper(
            this.connection,
            this.audienceFactory,
            LoggerFactory.getLogger(AudienceDataMapper.class));
    this.dataMappers.put(Notification.class, ndm);
    this.dataMappers.put(Audience.class, adm);
    this.dataMappers.put(Target.class, tdm);
  }

  @Override
  public Map<Class, DataMapper> dataMappers() {
    return this.dataMappers;
  }

  @Override
  public void save() {

    try {

      for (Entity entity : this.added()) {
        DataMapper dm = this.dataMappers.get(entity.getClass());
        dm.insert(entity);
      }

      for (Entity entity : this.altered()) {
        DataMapper dm = this.dataMappers.get(entity.getClass());
        dm.update(entity);
      }

      for (Entity entity : this.removed()) {
        DataMapper dm = this.dataMappers.get(entity.getClass());
        dm.delete((UUID) entity.getId());
      }

      this.connection.commit();
    } catch (SQLException x) {
      try {
        if (this.connection != null) {
          this.connection.rollback();
        }
      } catch (SQLException z) {
        throw new RuntimeException(z);
      }
    } finally {
      try {
        if (this.connection != null) {
          this.connection.close();
        }
      } catch (SQLException y) {
        throw new RuntimeException(y);
      }
    }
  }
}
