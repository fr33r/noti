package domain;

public class FailedState extends NotificationState {

  @Override
  public void next(Notification notification) {
    // valid transitions:
    //	--> (NONE) FailedState.
    notification.setState(this);
  }
}
