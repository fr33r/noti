package domain;

public class Message extends Entity<Integer> {

  private PhoneNumber from;
  private PhoneNumber to;
  private String content;
  private MessageStatus status;
  private String externalId;

  public PhoneNumber getFrom() {
    return from;
  }

  public void setFrom(PhoneNumber from) {
    this.from = from;
  }

  public void setStatus(MessageStatus status) {
    this.status = status;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public MessageStatus getStatus() {
    return status;
  }

  public String getExternalId() {
    return externalId;
  }

  public PhoneNumber getTo() {
    return to;
  }

  public void setTo(PhoneNumber to) {
    this.to = to;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Message(
      PhoneNumber from, PhoneNumber to, String content, MessageStatus status, String externalId) {
    super();
    this.from = from;
    this.to = to;
    this.content = content;
    this.status = status;
    this.externalId = externalId;
  }

  public Message(
      Integer id,
      PhoneNumber from,
      PhoneNumber to,
      String content,
      MessageStatus status,
      String externalId) {
    super(id);
    this.from = from;
    this.to = to;
    this.content = content;
    this.status = status;
    this.externalId = externalId;
  }

  @Override
  public boolean isAggregateRoot() {
    return false;
  }
}
