import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;

/**
 * Defines the abstraction of an {@link ApplicationModule} for the Noti application.
 *
 * @author Jon Freer
 */
public abstract class NotiModule implements ApplicationModule {

  private final Environment environment;
  private final NotiConfiguration configuration;

  /**
   * Constructs a new {@link NotiModule}.
   *
   * @param configuration The application configuration for the Noti application.
   * @param environment The application environment for the Noti application.
   */
  public NotiModule(NotiConfiguration configuration, Environment environment) {
    this.environment = environment;
    this.configuration = configuration;
  }

  /**
   * Retrieves the application environment.
   *
   * @return The application environment.
   */
  Environment getEnvironment() {
    return this.environment;
  }

  /**
   * Retrieves the application configuration.
   *
   * @return The application configuration.
   */
  NotiConfiguration getConfiguration() {
    return this.configuration;
  }

  /** Configures the application module. */
  @Override
  public abstract void configure();
}
