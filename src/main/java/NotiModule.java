import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;

/**
 * Represents an application module for the Noti application.
 *
 * @author Jon Freer
 */
public abstract class NotiModule implements ApplicationModule {

	private final Environment environment;
	private final NotiConfiguration configuration;

	/**
	 * Constructs an instance of {@link NotiModule}.
	 *
	 * @param configuration
	 * @param environment
	 */
	public NotiModule(NotiConfiguration configuration, Environment environment) {
		this.environment = environment;
		this.configuration = configuration;
	}

	/**
	 * Retrieves the application environment.
	 */
	Environment getEnvironment() {
		return this.environment;
	}

	/**
	 * Retrieves the application configuration.
	 */
	NotiConfiguration getConfiguration() {
		return this.configuration;
	}

	@Override
	public abstract void configure();
}
