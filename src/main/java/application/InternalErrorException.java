package application;

import java.util.ArrayList;
import java.util.List;

public final class InternalErrorException extends ApplicationException {

  public InternalErrorException(
      String message, String detailedMessage, String sillyMessage, String emoji) {
    super(message, detailedMessage, sillyMessage, emoji);
  }

  public InternalErrorException(String message, String detailedMessage) {
    super(message, detailedMessage);
  }

  @Override
  List<String> emojis() {
    List<String> emojis = new ArrayList<>();
    emojis.add("😵");
    emojis.add("💀");
    emojis.add("☠️");
    emojis.add("🤦");
    emojis.add("🙃");
    emojis.add("🤫");
    return emojis;
  }

  @Override
  List<String> sillyMessages() {
    List<String> sillyMessages = new ArrayList<>();
    sillyMessages.add("I'll give you a hint - retrying won't help.");
    sillyMessages.add("You asked me to do the one thing I couldn't do!");
    sillyMessages.add("Is it 5PM yet?");
    sillyMessages.add("Today has been a bad day.");
    sillyMessages.add("Try again never.");
    sillyMessages.add("My friends tell me I am great at this.");
    sillyMessages.add("Fail.");
    sillyMessages.add("This real question is: did I make a mistake, or am I just not trying?");
    return sillyMessages;
  }
}
