package infrastructure;

import java.util.UUID;

import org.jvnet.hk2.annotations.Contract;

import domain.Notification;
import domain.Target;

/**
 *	This is my go at creating an abstract factory! Let's hope it goes well.
 */
@Contract
public abstract class RepositoryFactory {

	//create abstract product A.
	//could change this to be "less" abstract by creating NotificationSQLRepository abstract class.
	public abstract Repository<Notification, UUID> createNotificationRepository(SQLUnitOfWork unitOfWork);

	//create abstract product B.
	//could change this to be "less" abstract by creating TargetSQLRepository abstract class.
	public abstract Repository<Target, UUID> createTargetRepository(SQLUnitOfWork unitOfWork);
}
