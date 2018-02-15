package application.services;

import infrastructure.SQLUnitOfWorkFactory;
import infrastructure.RepositoryFactory;
import javax.inject.Inject;

import domain.Notification;
import domain.NotificationFactory;
import infrastructure.Repository;
import java.util.UUID;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import infrastructure.SQLUnitOfWork;
import infrastructure.ShortMessageService;
import mappers.Mapper;
import domain.Message;

public final class NotificationService implements application.NotificationService{

	private SQLUnitOfWorkFactory unitOfWorkFactory;
	private RepositoryFactory repositoryFactory;
	private ShortMessageService shortMessageService;
	private NotificationFactory notificationFactory;
	private Mapper<Notification, api.representations.Notification> mapper;
	
	/**
	 * Constructs a {@link NotificationService} instance.
	 * @param unitOfWorkFactory The factory responsible for constructing instances of {@link SQLUnitOfWork}.
	 * @param repositoryFactory The factory responsible for constructing instances of {@link Repository}
	 * @param shortMessageService The infrastructure service responsible for sending and retrieving 
	 * short messages (text messages; AKA SMS).
	 */
	@Inject
	public NotificationService(
		SQLUnitOfWorkFactory unitOfWorkFactory, 
		RepositoryFactory repositoryFactory,
		ShortMessageService shortMessageService,
		NotificationFactory notificationFactory,
		Mapper<Notification, api.representations.Notification> mapper
	) {
		this.unitOfWorkFactory = unitOfWorkFactory;
		this.repositoryFactory = repositoryFactory;
		this.shortMessageService = shortMessageService;
		this.notificationFactory = notificationFactory;
		this.mapper = mapper;
	}

	/**
	 * Creates a new notification with the representation provided.
	 * @param notification The representation of the notification to create.
	 * @return The unique identifier assigned to the newly created notification.
	 */
	public UUID createNotification(api.representations.Notification notification) {
	
		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		Date now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		Notification noti_domain = 
			this.notificationFactory.createFrom(notification);
		
		try {
			
			Repository<Notification, UUID> notificationRepository = 
				this.repositoryFactory.createNotificationRepository(unitOfWork);

			long timeUntilSend = 0;
			if(noti_domain.sendAt() != null) {
				timeUntilSend = noti_domain.sendAt().getTime() - now.getTime();
			}

			if(timeUntilSend <= 0) {
				for(Message message : noti_domain.messages()){
					message = this.shortMessageService.send(message);
				}
			} else {
				//place them in queue to be processed later.
			}

			notificationRepository.add(noti_domain);

			unitOfWork.save();
			return noti_domain.getId();

		} catch (Exception x) {
			//log.
			//throw generic error with reason(s) that can easily be mapped to HTTP status codes.
			// --> for example NOT_FOUND
			x.printStackTrace();
			unitOfWork.undo();
			throw new RuntimeException(x);
		}
	}

	/**
	 * Retrieves the notification with the provided unique identifier.
	 * @param uuid The unique identifier for the notification to retrieve.
	 * @return A representation of the notification with the unique identifier provided.
	 */
	public api.representations.Notification getNotification(UUID uuid) {

		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		Notification noti_dm = null;

		try {
			Repository<Notification, UUID> notificationRepository = 
				this.repositoryFactory.createNotificationRepository(unitOfWork);
			noti_dm = notificationRepository.get(uuid);
			unitOfWork.save();
		} catch (Exception x) {
			unitOfWork.undo();
			throw x;
		}

		if (noti_dm == null) {
			throw new RuntimeException(String.format("Can't find notification with UUID of '%s'", uuid.toString()));
		}
		
		api.representations.Notification noti_sm = this.mapper.map(noti_dm);
		return noti_sm;
	}

	/**
	 * Deletes an existing notification.
	 * @param uuid The unique identifier for the notification to delete.
	 */
	public void deleteNotification(UUID uuid) {
		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		
		try {
			Repository<Notification, UUID> notificationRepository = 
				this.repositoryFactory.createNotificationRepository(unitOfWork);
			notificationRepository.remove(uuid);
			unitOfWork.save();
		} catch (Exception x) {
			unitOfWork.undo();
			throw x;
		}	
	}

	/**
	 * Replaces the existing state of a notification with the representation provided.
	 * @param notification The representation of the notification to overwrite the existing state.
	 */
	public void updateNotification(api.representations.Notification notification) {
		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();
		
		try {
			Repository<Notification, UUID> notificationRepository = 
				this.repositoryFactory.createNotificationRepository(unitOfWork);
			Notification noti_domain = this.notificationFactory.createFrom(notification);
			notificationRepository.put(noti_domain);
			unitOfWork.save();
		} catch (Exception x) {
			unitOfWork.undo();
			throw x;
		}
	}
}
