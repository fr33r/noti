import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;

public final class LoggingModule extends NotiModule {

	public LoggingModule(NotiConfiguration configuration, Environment environment) {
		super(configuration, environment);
	}

	@Override
	public void configure() {

		//TODO - determine if this is the right way to access the logger.
		//register logger with environment. TODO - is DI the way logger should be handled?
		this.getEnvironment().jersey().register(new AbstractBinder() {
			@Override
			protected void configure() {
				this.bind(LoggerFactory.getLogger(Noti.class)).to(Logger.class);
			}
		});
	}
}
