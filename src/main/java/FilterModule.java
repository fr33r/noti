import api.filters.CacheControlFilter;
import api.filters.ConditionalGetFilter;
import api.filters.ConditionalPutFilter;
import api.filters.MetadataDeleteFilter;
import api.filters.MetadataGetFilter;
import api.filters.MetadataPostFilter;
import api.filters.MetadataPutFilter;
import api.filters.VaryFilter;
import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;

public final class FilterModule extends NotiModule {

	public FilterModule(NotiConfiguration configuration, Environment environment) {
		super(configuration, environment);
	}

	@Override
	public void configure() {

		//register Jersey filters with environment.
		this.getEnvironment().jersey().register(CacheControlFilter.class);
		this.getEnvironment().jersey().register(ConditionalGetFilter.class);
		this.getEnvironment().jersey().register(ConditionalPutFilter.class);
		this.getEnvironment().jersey().register(VaryFilter.class);
		this.getEnvironment().jersey().register(MetadataGetFilter.class);
		this.getEnvironment().jersey().register(MetadataDeleteFilter.class);
		this.getEnvironment().jersey().register(MetadataPostFilter.class);
		this.getEnvironment().jersey().register(MetadataPutFilter.class);
	}
}
