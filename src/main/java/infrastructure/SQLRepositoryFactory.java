package infrastructure;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

import domain.EntitySQLFactory;
import domain.Notification;
import domain.Target;

@Service
public class SQLRepositoryFactory extends RepositoryFactory {

	private EntitySQLFactory<Notification, UUID> notificationFactory;
	private EntitySQLFactory<Target, UUID> targetFactory;

	@Inject
	public SQLRepositoryFactory(
		@Named("NotificationSQLFactory") EntitySQLFactory<Notification, UUID> notificationFactory,
		@Named("TargetSQLFactory") EntitySQLFactory<Target, UUID> targetFactory
	) {
		this.notificationFactory = notificationFactory;
		this.targetFactory = targetFactory;
	}

	@Override
	public Repository<Notification, UUID> createNotificationRepository(SQLUnitOfWork unitOfWork) {
		return new NotificationRepository(unitOfWork, this.notificationFactory); //both of these inherit from SQLRepository!
	}
	
	@Override
	public Repository<Target, UUID> createTargetRepository(SQLUnitOfWork unitOfWork) {
		return new TargetRepository(unitOfWork, this.targetFactory); //both of these inherit from SQLRepository!
	}
}
