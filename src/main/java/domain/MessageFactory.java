package domain;

public class MessageFactory {

  public Message createFrom(application.Message message) {
    MessageStatus status = MessageStatus.valueOf(message.getStatus().toString());
    return new Message(
        message.getID(),
        new PhoneNumber(message.getFrom()),
        new PhoneNumber(message.getTo()),
        message.getContent(),
        status,
        message.getExternalID());
  }
}
