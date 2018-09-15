package api.representations.json;

import api.representations.Representation;
import javax.ws.rs.core.MediaType;

public final class Message extends Representation {

  private Integer id;
  private String content;
  private String to;
  private String from;
  private MessageStatus status;
  private String externalID;

  public Message() {
    super(MediaType.APPLICATION_JSON_TYPE);
  }

  public static final class Builder extends Representation.Builder {

    private Integer id;
    private String content;
    private String to;
    private String from;
    private MessageStatus status;
    private String externalID;

    public Builder() {
      super(MediaType.APPLICATION_JSON_TYPE);
    }

    public Builder id(Integer id) {
      this.id = id;
      return this;
    }

    public Builder content(String content) {
      this.content = content;
      return this;
    }

    public Builder to(String to) {
      this.to = to;
      return this;
    }

    public Builder from(String from) {
      this.from = from;
      return this;
    }

    public Builder status(MessageStatus status) {
      this.status = status;
      return this;
    }

    public Builder externalID(String externalID) {
      this.externalID = externalID;
      return this;
    }

    @Override
    public Representation build() {
      Message m = new Message();
      m.setLocation(this.location());
      m.setEncoding(this.encoding());
      m.setLanguage(this.language());
      m.setLastModified(this.lastModified());
      m.setID(this.id);
      m.setTo(this.to);
      m.setFrom(this.from);
      m.setContent(this.content);
      m.setExternalID(this.externalID);
      m.setStatus(this.status);
      return m;
    }
  }

  public Integer getID() {
    return this.id;
  }

  private void setID(Integer id) {
    this.id = id;
  }

  public String getTo() {
    return this.to;
  }

  private void setTo(String to) {
    this.to = to;
  }

  public String getFrom() {
    return this.from;
  }

  private void setFrom(String from) {
    this.from = from;
  }

  public String getContent() {
    return this.content;
  }

  private void setContent(String content) {
    this.content = content;
  }

  public String getExternalID() {
    return this.externalID;
  }

  private void setExternalID(String externalID) {
    this.externalID = externalID;
  }

  public MessageStatus getStatus() {
    return this.status;
  }

  private void setStatus(MessageStatus status) {
    this.status = status;
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
