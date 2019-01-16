package domain;

public class SentState extends NotificationState {

  @Override
  public void next(final Notification notification) {
    // valid transitions:
    //	--> (NONE) SENDING.
    notification.setState(this);
  }
}
