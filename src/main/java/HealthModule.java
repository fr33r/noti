import api.health.DatabaseHealthCheck;
import configuration.DatabaseConfiguration;
import configuration.NotiConfiguration;
import infrastructure.ConnectionFactory;
import infrastructure.MySQLConnectionFactory;
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

    ConnectionFactory connectionFactory =
        new MySQLConnectionFactory(url, username, password, GlobalTracer.get());
    this.getEnvironment()
        .healthChecks()
        .register("database", new DatabaseHealthCheck(connectionFactory));
  }
}
