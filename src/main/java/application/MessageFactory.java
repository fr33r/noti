package application;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import org.slf4j.Logger;

public class MessageFactory {

  private Logger logger;

  @Inject
  public MessageFactory(Logger logger) {
    this.logger = logger;
  }

  public Message createFrom(api.representations.xml.Message message) {
    MessageStatus status = MessageStatus.valueOf(message.getStatus().toString());
    return new Message(
        message.getID(),
        message.getContent(),
        message.getTo(),
        message.getFrom(),
        status,
        message.getExternalID());
  }

  public Message createFrom(api.representations.json.Message message) {
    MessageStatus status = MessageStatus.valueOf(message.getStatus().toString());
    return new Message(
        message.getID(),
        message.getContent(),
        message.getTo(),
        message.getFrom(),
        status,
        message.getExternalID());
  }

  public Message createFrom(api.representations.json.TwilioMessageLog log, Message message) {
    MessageStatus status = MessageStatus.valueOf(log.getMessageStatus().toString());
    return new Message(
        message.getID(),
        message.getContent(),
        message.getTo(),
        message.getFrom(),
        status,
        message.getExternalID());
  }

  public Message createFrom(MultivaluedMap<String, String> log, Message message) {
    String s = log.getFirst("MessageStatus");
    s = s.toUpperCase();
    MessageStatus status = MessageStatus.valueOf(s);
    return new Message(
        message.getID(),
        message.getContent(),
        message.getTo(),
        message.getFrom(),
        status,
        message.getExternalID());
  }

  public Message createFrom(domain.Message message) {
    MessageStatus status = MessageStatus.valueOf(message.getStatus().toString());
    return new Message(
        message.getId(),
        message.getContent(),
        message.getTo().toE164(),
        message.getFrom().toE164(),
        status,
        message.getExternalId());
  }
}
