package application;

import java.util.ArrayList;
import java.util.List;

public final class NotFoundException extends ApplicationException {

  public NotFoundException(
      String message, String detailedMessage, String sillyMessage, String emoji) {
    super(message, detailedMessage, sillyMessage, emoji);
  }

  public NotFoundException(String message, String detailedMessage) {
    super(message, detailedMessage);
  }

  @Override
  List<String> emojis() {
    List<String> emojis = new ArrayList<>();
    emojis.add("🔍");
    emojis.add("🔎");
    emojis.add("🕵️");
    emojis.add("🕵️");
    return emojis;
  }

  @Override
  List<String> sillyMessages() {
    List<String> sillyMessages = new ArrayList<>();
    sillyMessages.add("I couldn't find the droid I was looking for.");
    sillyMessages.add("Nobody told me I needed to keep track of it!");
    sillyMessages.add("...Completely forget where I put that.");
    sillyMessages.add("I couldn't find that, but I found a whole lot of other stuff.");
    return sillyMessages;
  }
}
