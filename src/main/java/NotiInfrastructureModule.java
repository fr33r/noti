import configuration.NotiConfiguration;
import domain.Notification;
import infrastructure.MessageQueueService;
import infrastructure.MySQLUnitOfWorkFactory;
import infrastructure.NotificationQueryFactory;
import infrastructure.QueryFactory;
import infrastructure.RepositoryFactory;
import infrastructure.SQLRepositoryFactory;
import infrastructure.SQLUnitOfWorkFactory;
import infrastructure.services.RepresentationMetadataService;
import infrastructure.services.SMSQueueService;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public final class NotiInfrastructureModule extends NotiModule {

  public NotiInfrastructureModule(NotiConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {

    // register infrastructure layer components with environment.
    this.getEnvironment()
        .jersey()
        .register(
            new AbstractBinder() {
              @Override
              protected void configure() {

                this.bind(SQLRepositoryFactory.class).to(RepositoryFactory.class);
                this.bind(MySQLUnitOfWorkFactory.class).to(SQLUnitOfWorkFactory.class);
                this.bind(RepresentationMetadataService.class)
                    .to(infrastructure.RepresentationMetadataService.class);
                this.bind(SMSQueueService.class).to(MessageQueueService.class);
                this.bind(NotificationQueryFactory.class)
                    .to(new TypeLiteral<QueryFactory<Notification>>() {});
              }
            });
  }
}
