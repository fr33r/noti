package application;

public final class Message {

  private Integer id;
  private String content;
  private String to;
  private String from;
  private MessageStatus status;
  private String externalID;

  public Message() {}

  public Message(
      Integer id, String content, String to, String from, MessageStatus status, String externalID) {

    this.id = id;
    this.content = content;
    this.to = to;
    this.from = from;
    this.status = status;
    this.externalID = externalID;
  }

  public Integer getID() {
    return this.id;
  }

  public String getTo() {
    return this.to;
  }

  public String getFrom() {
    return this.from;
  }

  public String getContent() {
    return this.content;
  }

  public String getExternalID() {
    return this.externalID;
  }

  public MessageStatus getStatus() {
    return this.status;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != this.getClass()) return false;
    Message message = (Message) obj;

    boolean sameID =
        message.getID() == null && this.getID() == null
            || message.getID() != null
                && this.getID() != null
                && message.getID().equals(this.getID());
    boolean sameContent =
        message.getContent() == null && this.getContent() == null
            || message.getContent() != null
                && this.getContent() != null
                && message.getContent().equals(this.getContent());
    boolean sameFrom =
        message.getFrom() == null && this.getFrom() == null
            || message.getFrom() != null
                && this.getFrom() != null
                && message.getFrom().equals(this.getFrom());
    boolean sameTo =
        message.getTo() == null && this.getTo() == null
            || message.getTo() != null
                && this.getTo() != null
                && message.getTo().equals(this.getTo());
    boolean sameExternalID =
        message.getExternalID() == null && this.getExternalID() == null
            || message.getExternalID() != null
                && this.getExternalID() != null
                && message.getExternalID().equals(this.getExternalID());
    boolean sameStatus =
        message.getStatus() == null && this.getStatus() == null
            || message.getStatus() != null
                && this.getStatus() != null
                && message.getStatus().equals(this.getStatus());

    return sameID && sameContent && sameFrom && sameTo && sameExternalID && sameStatus;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    final int prime = 17;

    if (this.getID() != null) {
      hashCode = hashCode * prime + this.getID().hashCode();
    }

    if (this.getFrom() != null) {
      hashCode = hashCode * prime + this.getFrom().hashCode();
    }

    if (this.getTo() != null) {
      hashCode = hashCode * prime + this.getTo().hashCode();
    }

    if (this.getContent() != null) {
      hashCode = hashCode * prime + this.getContent().hashCode();
    }

    if (this.getExternalID() != null) {
      hashCode = hashCode * prime + this.getExternalID().hashCode();
    }

    if (this.getStatus() != null) {
      hashCode = hashCode * prime + this.getStatus().hashCode();
    }

    return hashCode;
  }
}
