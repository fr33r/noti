package domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

/**
 * NOT WHAT ENVISION AS THE RIGHT SOLUTION.
 * SEE PAGE 143 of DDD.
 * THIS FACTORY IS NOT USING ABSTRACTIONS AS RETURN TYPES.
 * COULDNT THINK OF A WAY AROUND THIS, SINCE CLIENTS OF THIS FACTORY MUST
 * HAVE CONCRETE NOTIFICATION CLASS (NOTIFICATIONREPOSITORY FOR EXAMPLE).
 */
@Service
@Named("NotificationSQLFactory")
public class NotificationSQLFactory extends EntitySQLFactory<Notification, UUID>{

	private static final String uuidColumn			= "uuid";
	private static final String messageColumn		= "message";
	private static final String sentAtColumn			= "sent_at";
	private static final String statusColumn			= "status";
	private static final String sendAtColumn			= "send_at";
	private static final String nameColumn			= "name";
	private static final String phoneNumberColumn	= "phone_number";
	private static final String targetUUIDColumn		= "target_uuid";
	private static final String idColumn				= "id";
	private static final String fromColumn			= "from";
	private static final String toColumn				= "to";
	private static final String contentColumn		= "content";
	private static final String externalIdColumn		= "external_id";
	private static final Calendar utc				= 
		Calendar.getInstance(TimeZone.getTimeZone("UTC"));

	@Inject
	public NotificationSQLFactory(){}

	public Notification reconstitute(Statement statement) {

		if(statement == null) { return null; }
		
		Notification notification = null;
		Set<Target> targets = null;
		Map<UUID, Set<Tag>> tags = null;
		try{
			if(statement.isClosed()) { return null; }

			notification = this.extractNotification(statement.getResultSet());
			if(statement.getMoreResults()){
				targets = this.extractTargets(statement.getResultSet());
				for(Target target : targets){
					notification.includeRecipient(target);
				}
			}

			if(statement.getMoreResults()){
				tags = this.extractTags(statement.getResultSet());
				for(Target target : notification.directRecipients()){
					if(tags.containsKey(target.getId())){
						for(Tag tag : tags.get(target.getId())) {
							target.tag(tag);
						}
					}
				}	
			}

		} catch (SQLException x){
			throw new RuntimeException(x);
		} 
		return notification;
	}

	private Map<UUID, Set<Tag>> extractTags(ResultSet results) throws SQLException {
		Map<UUID, Set<Tag>> tagsForTargets = new HashMap<>();
		while(results.next()){
			String targetUUID = results.getString(targetUUIDColumn);
			String tagName = results.getString(nameColumn);
			if(!tagsForTargets.containsKey(UUID.fromString(targetUUID))){
				Set<Tag> tags = new HashSet<>();
				tags.add(new Tag(tagName));
				tagsForTargets.put(UUID.fromString(targetUUID), tags);
			}else{
				tagsForTargets.get(UUID.fromString(targetUUID)).add(new Tag(tagName));
			}
		}
		return tagsForTargets;
	}

	private Set<Target> extractTargets(ResultSet results) throws SQLException {
		Set<Target> targets = new HashSet<>();
		while(results.next()){
			String uuid = results.getString(uuidColumn);
			String name = results.getString(nameColumn);
			String phoneNumber = results.getString(phoneNumberColumn);
			targets.add(new Target(UUID.fromString(uuid), name, new PhoneNumber(phoneNumber)));
		}
		return targets;
	}

	private Notification extractNotification(ResultSet results) throws SQLException {
		Notification notification = null;

		//notification results.
		if(results.next()){
			String uuid = results.getString(uuidColumn);
			String content = results.getString(messageColumn);
			Timestamp sentAtTimestamp = results.getTimestamp(sentAtColumn, utc);
			Date sentAt = null;
			if(sentAtTimestamp != null) {
				sentAt = new Date(sentAtTimestamp.getTime());
			}
			String status = results.getString(statusColumn);
			Timestamp sendAtTimestamp = results.getTimestamp(sendAtColumn, utc);
			Date sendAt = null;
			if(sendAtTimestamp != null) {
				sendAt = new Date(sendAtTimestamp.getTime());
			}

			//compare persisted state with computed state. log error and adopt computed.
			NotificationStatus statusEnum = NotificationStatus.valueOf(status);

			NotificationBuilder builder = new NotificationBuilder();
			return builder.identity(uuid).content(content).sendAt(sendAt).sentAt(sentAt).build();
		}

		return notification;
	}
	
	private Set<Message> extractMessages(ResultSet results) throws SQLException {
		Set<Message> messages = new HashSet<Message>();
		
		//message results.
		while(results.next()) {
			Integer id = results.getInt(idColumn);
			String from = results.getString(fromColumn);
			String to = results.getString(toColumn);
			String content = results.getString(contentColumn);
			String status = results.getString(statusColumn);
			String externalId = results.getString(externalIdColumn);
			messages.add(
				new Message(
					id, 
					new PhoneNumber(from), 
					new PhoneNumber(to), 
					content, 
					MessageStatus.valueOf(status), 
					externalId
				)
			);
		}
		
		return messages;
	}

	@Override
	public Notification reconstitute(ResultSet... results) {
		
		if (results == null || results.length < 1) {
			return null;
		}

		Notification notification = null;
		Set<Target> targets = null;
		Map<UUID, Set<Tag>> tags = null;
		Set<Message> messages = null;
		try{

			notification = this.extractNotification(results[0]);
			if(results.length > 1){
				targets = this.extractTargets(results[1]);
				for(Target target : targets){
					notification.includeRecipient(target);
				}
			}

			if(results.length > 2){
				tags = this.extractTags(results[2]);
				for(Target target : targets){
					if(tags.containsKey(target.getId())){
						for(Tag tag : tags.get(target.getId())) {
							target.tag(tag);
						}
					}
				}	
			}
			
			if(results.length > 3){
				messages = this.extractMessages(results[3]);
				notification.messages(messages);
			}

		} catch (SQLException x){
			throw new RuntimeException(x);
		} 
		return notification;
	}
}
