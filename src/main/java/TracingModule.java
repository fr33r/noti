import com.uber.jaeger.context.TracingUtils;
import com.uber.jaeger.dropwizard.Configuration;
import com.uber.jaeger.dropwizard.JaegerFeature;
import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public final class TracingModule extends NotiModule {

  public TracingModule(NotiConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {

    // extract the configuration.
    Configuration jaegerConfiguration = this.getConfiguration().getJaegerConfiguration();

    // construct feature.
    JaegerFeature jaegerFeature = new JaegerFeature(jaegerConfiguration);

    // retrieve the tracer.
    Tracer tracer = jaegerConfiguration.getTracer();
    GlobalTracer.register(tracer); // TODO - investigate the need for this.
    TracingUtils.setTracer(tracer); // TODO - investigate the need for this.

    // register tracer with environment.
    AbstractBinder binder =
        new AbstractBinder() {
          @Override
          protected void configure() {
            this.bind(tracer).to(Tracer.class);
          }
        };
    this.getEnvironment().jersey().register(binder);

    // register feature with environment.
    this.getEnvironment().jersey().register(jaegerFeature);
  }
}
