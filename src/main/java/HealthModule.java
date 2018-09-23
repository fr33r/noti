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
    final String host = databaseConfiguration.getHost();
    final int port = databaseConfiguration.getPort();
    final String name = databaseConfiguration.getName();
    final boolean useLegacyDateTimeCode = databaseConfiguration.getUseLegacyDatetimeCode();
    final boolean useSSL = databaseConfiguration.getUseSSL();
    final String urlTemplate = "jdbc:mysql://%s:%s/%s?useLegacyDatetimeCode=%b&useSSL=%b";
    final String url = String.format(urlTemplate, host, port, name, useLegacyDateTimeCode, useSSL);

    SQLUnitOfWorkFactory unitOfWorkFactory =
        new MySQLUnitOfWorkFactory(url, username, password, GlobalTracer.get());
    this.getEnvironment()
        .healthChecks()
        .register("database", new DatabaseHealthCheck(unitOfWorkFactory));
  }
}
