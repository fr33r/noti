import api.health.DatabaseHealthCheck;
import configuration.DatabaseConfiguration;
import configuration.NotiConfiguration;
import infrastructure.MySQLUnitOfWorkFactory;
import infrastructure.SQLUnitOfWorkFactory;
import io.dropwizard.setup.Environment;
import io.opentracing.util.GlobalTracer;

public final class HealthModule extends NotiModule {

  public HealthModule(NotiConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {
    DatabaseConfiguration databaseConfiguration =
        this.getConfiguration().getDatabaseConfiguration();
    final String username = databaseConfiguration.getUser();
    final String password = databaseConfiguration.getPassword();
    final String url = databaseConfiguration.getURL();

    SQLUnitOfWorkFactory unitOfWorkFactory =
        new MySQLUnitOfWorkFactory(url, username, password, GlobalTracer.get());
    this.getEnvironment()
        .healthChecks()
        .register("database", new DatabaseHealthCheck(unitOfWorkFactory));
  }
}
