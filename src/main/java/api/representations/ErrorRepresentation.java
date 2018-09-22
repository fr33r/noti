package api.representations;

import application.ApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
public final class ErrorRepresentation extends Representation {

  private String message;
  private String detailedMessage;
  private String sillyMessage;
  private String emoji;

  public ErrorRepresentation() {
    super();
  }

  public ErrorRepresentation(MediaType mediaType, ApplicationException x) {
    super(mediaType);
    this.setMessage(x.getMessage());
    this.setDetailedMessage(x.getDetailedMessage());
    this.setSillyMessage(x.getSillyMessage());
    this.setEmoji(x.getEmoji());
  }

  @XmlElement
  public String getMessage() {
    return this.message;
  }

  private void setMessage(String message) {
    this.message = message;
  }

  @XmlElement
  public String getDetailedMessage() {
    return this.detailedMessage;
  }

  private void setDetailedMessage(String detailedMessage) {
    this.detailedMessage = detailedMessage;
  }

  @XmlElement
  public String getSillyMessage() {
    return this.sillyMessage;
  }

  private void setSillyMessage(String sillyMessage) {
    this.sillyMessage = sillyMessage;
  }

  @XmlElement
  public String getEmoji() {
    return this.emoji;
  }

  private void setEmoji(String emoji) {
    this.emoji = emoji;
  }
}
