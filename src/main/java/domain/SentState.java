package domain;

public class SentState extends NotificationState {

  @Override
  public void changeStatus(final Notification notification) {
    // valid transitions:
    //	--> (NONE) SENDING.
  }
}
