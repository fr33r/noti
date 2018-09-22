import api.filters.ConditionalGetFilter;
import api.filters.ConditionalPutFilter;
import api.interceptors.MetadataGetInterceptor;
import api.interceptors.MetadataPutInterceptor;
import application.services.AudienceService;
import application.services.NotificationService;
import application.services.TargetService;
import configuration.NotiConfiguration;
import infrastructure.services.RepresentationMetadataService;
import infrastructure.services.SMSQueueService;
import io.dropwizard.setup.Environment;
import java.util.ArrayList;
import java.util.List;
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
                List<Logger> loggers = new ArrayList<>();
                loggers.add(LoggerFactory.getLogger(Noti.class));
                loggers.add(LoggerFactory.getLogger(ConditionalGetFilter.class));
                loggers.add(LoggerFactory.getLogger(ConditionalPutFilter.class));
                loggers.add(LoggerFactory.getLogger(MetadataGetInterceptor.class));
                loggers.add(LoggerFactory.getLogger(MetadataPutInterceptor.class));
                loggers.add(LoggerFactory.getLogger(NotificationService.class));
                loggers.add(LoggerFactory.getLogger(AudienceService.class));
                loggers.add(LoggerFactory.getLogger(TargetService.class));
                loggers.add(LoggerFactory.getLogger(SMSQueueService.class));
                loggers.add(LoggerFactory.getLogger(RepresentationMetadataService.class));

                // wire up logger instances.
                for (Logger logger : loggers) {
                  this.bind(logger).to(Logger.class).named(logger.getName());
                }
              }
            });
  }
}
