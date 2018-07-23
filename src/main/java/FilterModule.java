import api.filters.CacheControlFilter;
import api.filters.ConditionalGetFilter;
import api.filters.ConditionalPutFilter;
import api.filters.MetadataDeleteFilter;
import api.interceptors.MetadataGetInterceptor;
import api.interceptors.MetadataPutInterceptor;
import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;

public final class FilterModule extends NotiModule {

  public FilterModule(NotiConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {

    // register Jersey filters with environment.
    this.getEnvironment().jersey().register(CacheControlFilter.class);
    this.getEnvironment().jersey().register(ConditionalGetFilter.class);
    this.getEnvironment().jersey().register(ConditionalPutFilter.class);
    this.getEnvironment().jersey().register(MetadataDeleteFilter.class);
    this.getEnvironment().jersey().register(MetadataPutInterceptor.class);
    this.getEnvironment().jersey().register(MetadataGetInterceptor.class);
  }
}
