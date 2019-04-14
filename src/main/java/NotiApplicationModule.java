import application.AudienceFactory;
import application.MessageFactory;
import application.NotificationFactory;
import application.TargetFactory;
import application.TemplateFactory;
import application.services.AudienceService;
import application.services.NotificationService;
import application.services.TargetService;
import application.services.TemplateService;
import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public final class NotiApplicationModule extends NotiModule {

  public NotiApplicationModule(NotiConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {

    // register application layer components in environment.
    this.getEnvironment()
        .jersey()
        .register(
            new AbstractBinder() {
              @Override
              protected void configure() {

                this.bind(NotificationService.class).to(application.NotificationService.class);
                this.bind(TargetService.class).to(application.TargetService.class);
                this.bind(AudienceService.class).to(application.AudienceService.class);
                this.bind(TemplateService.class).to(application.TemplateService.class);

                this.bindAsContract(NotificationFactory.class);
                this.bindAsContract(TargetFactory.class);
                this.bindAsContract(AudienceFactory.class);
                this.bindAsContract(MessageFactory.class);
                this.bindAsContract(TemplateFactory.class);
              }
            });
  }
}
