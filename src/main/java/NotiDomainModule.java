import configuration.NotiConfiguration;
import domain.Audience;
import domain.AudienceFactory;
import domain.AudienceSQLFactory;
import domain.EntitySQLFactory;
import domain.MessageFactory;
import domain.Notification;
import domain.NotificationFactory;
import domain.NotificationSQLFactory;
import domain.Target;
import domain.TargetFactory;
import domain.TargetSQLFactory;
import io.dropwizard.setup.Environment;
import java.util.UUID;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public final class NotiDomainModule extends NotiModule {

  private static final String NOTIFICATION_SQL_FACTORY = "NotificationSQLFactory";
  private static final String TARGET_SQL_FACTORY = "TargetSQLFactory";
  private static final String AUDIENCE_SQL_FACTORY = "AudienceSQLFactory";

  public NotiDomainModule(NotiConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {

    // register domain components with environment.
    this.getEnvironment()
        .jersey()
        .register(
            new AbstractBinder() {
              @Override
              protected void configure() {
                this.bind(NotificationSQLFactory.class)
                    .named(NOTIFICATION_SQL_FACTORY)
                    .to(new TypeLiteral<EntitySQLFactory<Notification, UUID>>() {});
                this.bind(TargetSQLFactory.class)
                    .named(TARGET_SQL_FACTORY)
                    .to(new TypeLiteral<EntitySQLFactory<Target, UUID>>() {});

                this.bind(AudienceSQLFactory.class)
                    .named(AUDIENCE_SQL_FACTORY)
                    .to(new TypeLiteral<EntitySQLFactory<Audience, UUID>>() {});

                this.bindAsContract(NotificationFactory.class);
                this.bindAsContract(TargetFactory.class);
                this.bindAsContract(AudienceFactory.class);
                this.bindAsContract(MessageFactory.class);
              }
            });
  }
}
