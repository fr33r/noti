package api.representations.json;

import api.representations.Representation;
import application.ApplicationException;
import javax.ws.rs.core.MediaType;

public final class Error extends Representation {

  private String message;
  private String detailedMessage;
  private String sillyMessage;
  private String emoji;

  public Error(ApplicationException x) {
    super(MediaType.APPLICATION_JSON_TYPE);
    this.setMessage(x.getMessage());
    this.setDetailedMessage(x.getDetailedMessage());
    this.setSillyMessage(x.getSillyMessage());
    this.setEmoji(x.getEmoji());
  }

  public String getMessage() {
    return this.message;
  }

  private void setMessage(String message) {
    this.message = message;
  }

  public String getDetailedMessage() {
    return this.detailedMessage;
  }

  private void setDetailedMessage(String detailedMessage) {
    this.detailedMessage = detailedMessage;
  }

  public String getSillyMessage() {
    return this.sillyMessage;
  }

  private void setSillyMessage(String sillyMessage) {
    this.sillyMessage = sillyMessage;
  }

  public String getEmoji() {
    return this.emoji;
  }

  private void setEmoji(String emoji) {
    this.emoji = emoji;
  }
}
