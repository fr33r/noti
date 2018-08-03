package domain;

public class PendingState extends NotificationState {

  @Override
  public void changeStatus(Notification notification) {

    // valid transitions:
    //	-->	SENDING.
    //	-->	FAILED.
    //	--> (NONE) PENDING.

    if (this.changeToFailedState(notification)) {
      notification.status(NotificationStatus.FAILED);
      notification.setState(new FailedState());
    } else if (this.changeToSendingState(notification)) {
      notification.status(NotificationStatus.SENDING);
      notification.setState(new SendingState());
    }
  }
}
