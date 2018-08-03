package domain;

public class FailedState extends NotificationState {

  @Override
  public void changeStatus(Notification notification) {
    // valid transitions:
    //	--> (NONE) FailedState.
  }
}
