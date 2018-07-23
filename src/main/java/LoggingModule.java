import api.filters.ConditionalGetFilter;
import api.filters.ConditionalPutFilter;
import api.interceptors.MetadataGetInterceptor;
import api.interceptors.MetadataPutInterceptor;
import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggingModule extends NotiModule {

  public LoggingModule(NotiConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {

    this.getEnvironment()
        .jersey()
        .register(
            new AbstractBinder() {
              @Override
              protected void configure() {

                // retrieve logger instances.
                final Logger notiLogger = LoggerFactory.getLogger(Noti.class);
                final Logger conditionalGetFilterLogger =
                    LoggerFactory.getLogger(ConditionalGetFilter.class);
                final Logger conditionalPutFilterLogger =
                    LoggerFactory.getLogger(ConditionalPutFilter.class);
                final Logger metadataGetInterceptorLogger =
                    LoggerFactory.getLogger(MetadataGetInterceptor.class);
                final Logger metadataPutInterceptorLogger =
                    LoggerFactory.getLogger(MetadataPutInterceptor.class);

                // wire up logger instances.
                this.bind(notiLogger).to(Logger.class).named(notiLogger.getName());
                this.bind(conditionalGetFilterLogger)
                    .to(Logger.class)
                    .named(conditionalGetFilterLogger.getName());
                this.bind(conditionalPutFilterLogger)
                    .to(Logger.class)
                    .named(conditionalPutFilterLogger.getName());
                this.bind(metadataGetInterceptorLogger)
                    .to(Logger.class)
                    .named(metadataGetInterceptorLogger.getName());
                this.bind(metadataPutInterceptorLogger)
                    .to(Logger.class)
                    .named(metadataPutInterceptorLogger.getName());
              }
            });
  }
}
