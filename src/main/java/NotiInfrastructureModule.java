import org.glassfish.hk2.utilities.binding.AbstractBinder;

import configuration.NotiConfiguration;
import infrastructure.MessageQueueService;
import infrastructure.MySQLUnitOfWorkFactory;
import infrastructure.RepositoryFactory;
import infrastructure.SQLRepositoryFactory;
import infrastructure.SQLUnitOfWorkFactory;
import infrastructure.services.EntityTagService;
import infrastructure.services.ResourceMetadataService;
import infrastructure.services.SMSQueueService;
import io.dropwizard.setup.Environment;

public final class NotiInfrastructureModule extends NotiModule {

	public NotiInfrastructureModule(
		NotiConfiguration configuration,
		Environment environment
	) {
		super(configuration, environment);
	}

	@Override
	public void configure() {

		//register infrastructure layer components with environment.
		this.getEnvironment().jersey().register(new AbstractBinder() {
			@Override
			protected void configure() {

				this.bind(SQLRepositoryFactory.class)
					.to(RepositoryFactory.class);

				this.bind(MySQLUnitOfWorkFactory.class)
					.to(SQLUnitOfWorkFactory.class);

				this.bind(ResourceMetadataService.class)
					.to(infrastructure.ResourceMetadataService.class);

				this.bind(EntityTagService.class)
					.to(infrastructure.EntityTagService.class);

				this.bind(SMSQueueService.class)
					.to(MessageQueueService.class);
			}
		});
	}
}