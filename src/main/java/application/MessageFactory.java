package application;

public class MessageFactory {

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
