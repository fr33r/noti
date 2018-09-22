package application;

import java.util.Date;
import java.util.List;
import java.util.Random;

public abstract class ApplicationException extends RuntimeException {

  private String detailedMessage;
  private String sillyMessage;
  private String emoji;

  public ApplicationException(
      String message, String detailedMessage, String sillyMessage, String emoji) {
    super(message);
    this.setDetailedMessage(detailedMessage);
    this.setSillyMessage(sillyMessage);
    this.setEmoji(emoji);
  }

  public ApplicationException(String message, String detailedMessage) {
    super(message);
    this.detailedMessage = detailedMessage;
    this.sillyMessage = this.choose(this.sillyMessages());
    this.emoji = this.choose(this.emojis());
  }

  private void setDetailedMessage(String detailedMessage) {
    this.detailedMessage = detailedMessage;
  }

  public String getDetailedMessage() {
    return this.detailedMessage;
  }

  private void setSillyMessage(String sillyMessage) {
    this.sillyMessage = sillyMessage;
  }

  public String getSillyMessage() {
    return this.sillyMessage;
  }

  private void setEmoji(String emoji) {
    this.emoji = emoji;
  }

  public String getEmoji() {
    return this.emoji;
  }

  private String choose(List<String> choices) {
    Random random = new Random(new Date().getTime());
    int choice = random.nextInt(choices.size());
    return choices.get(choice);
  }

  abstract List<String> emojis();

  abstract List<String> sillyMessages();
}
