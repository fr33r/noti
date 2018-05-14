import api.resources.AudienceResource;
import api.resources.NotificationResource;
import api.resources.TargetResource;
import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;

public final class ResourceModule extends NotiModule {

	public ResourceModule(NotiConfiguration configuration, Environment environment) {
		super(configuration, environment);
	}

	@Override
	public void configure() {

		//register Jersey resources with environment;
		this.getEnvironment().jersey().register(NotificationResource.class);
		this.getEnvironment().jersey().register(AudienceResource.class);
		this.getEnvironment().jersey().register(TargetResource.class);
	}
}
