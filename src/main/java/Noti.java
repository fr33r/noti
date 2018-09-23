import configuration.NotiConfiguration;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.ArrayList;
import java.util.List;

public class Noti extends Application<NotiConfiguration> {

  @Override
  public String getName() {
    return "noti";
  }

  @Override
  public void initialize(Bootstrap<NotiConfiguration> bootstrap) {
    bootstrap.setConfigurationSourceProvider(
        new SubstitutingSourceProvider(
            bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor()));
  }

  @Override
  public void run(NotiConfiguration configuration, Environment environment) throws Exception {

    // configure application modules.
    List<ApplicationModule> modules = new ArrayList<ApplicationModule>();
    modules.add(new KafkaModule(configuration, environment));
    modules.add(new MetricsModule(configuration, environment));
    modules.add(new TracingModule(configuration, environment));
    modules.add(new DatabaseModule(configuration, environment));
    modules.add(new ResourceModule(configuration, environment));
    modules.add(new FilterModule(configuration, environment));
    modules.add(new RepresentationModule(configuration, environment));
    modules.add(new LoggingModule(configuration, environment));
    modules.add(new NotiDomainModule(configuration, environment));
    modules.add(new NotiApplicationModule(configuration, environment));
    modules.add(new NotiInfrastructureModule(configuration, environment));
    modules.add(new HealthModule(configuration, environment));

    for (ApplicationModule module : modules) {
      module.configure();
    }
  }

  public static void main(String[] args) throws Exception {
    new Noti().run(args);
  }
}
