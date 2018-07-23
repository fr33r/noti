import configuration.DatabaseConfiguration;
import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public final class DatabaseModule extends NotiModule {

  private static final String JDBC_URL = "JDBC_URL";
  private static final String JDBC_USERNAME = "JDBC_USERNAME";
  private static final String JDBC_PASSWORD = "JDBC_PASSWORD";

  public DatabaseModule(NotiConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() throws FlywayException {

    // extract configuration.
    final DatabaseConfiguration databaseConfiguration =
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

    // setup the database.
    Flyway flyway = new Flyway();
    flyway.setInstalledBy(username);
    flyway.setDataSource(url, username, password);
    flyway.migrate();

    // register database configuration with environment.
    AbstractBinder binder =
        new AbstractBinder() {
          @Override
          protected void configure() {
            this.bind(url).to(String.class).named(JDBC_URL);
            this.bind(username).to(String.class).named(JDBC_USERNAME);
            this.bind(password).to(String.class).named(JDBC_PASSWORD);
          }
        };
    this.getEnvironment().jersey().register(binder);
  }
}
