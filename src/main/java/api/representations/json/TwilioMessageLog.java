package api.representations.json;

import api.representations.Representation;
import javax.ws.rs.core.MediaType;

public final class TwilioMessageLog extends Representation {

  private MessageStatus messageStatus;

  public TwilioMessageLog() {
    super(MediaType.APPLICATION_JSON_TYPE);
  }

  public static final class Builder extends Representation.Builder {

    private MessageStatus messageStatus;

    public Builder() {
      super(MediaType.APPLICATION_JSON_TYPE);
    }

    public Builder messageStatus(MessageStatus status) {
      this.messageStatus = status;
      return this;
    }

    @Override
    public Representation build() {
      TwilioMessageLog l = new TwilioMessageLog();
      l.setLocation(this.location());
      l.setEncoding(this.encoding());
      l.setLanguage(this.language());
      l.setLastModified(this.lastModified());
      l.setMessageStatus(this.messageStatus);
      return l;
    }
  }

  public MessageStatus getMessageStatus() {
    return this.messageStatus;
  }

  private void setMessageStatus(MessageStatus status) {
    this.messageStatus = status;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != this.getClass()) return false;
    TwilioMessageLog log = (TwilioMessageLog) obj;

    boolean sameStatus =
        log.getMessageStatus() == null && this.getMessageStatus() == null
            || log.getMessageStatus() != null
                && this.getMessageStatus() != null
                && log.getMessageStatus().equals(this.getMessageStatus());

    return sameStatus;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    final int prime = 17;

    if (this.getMessageStatus() != null) {
      hashCode = hashCode * prime + this.getMessageStatus().hashCode();
    }

    return hashCode;
  }
}
