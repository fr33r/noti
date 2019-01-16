package domain;

public class PendingState extends NotificationState {

  @Override
  public void next(Notification notification) {

    // valid transitions:
    //	-->	SENDING.
    //	-->	FAILED.
    //	--> (NONE) PENDING.

    if (this.failed(notification)) {
      notification.status(NotificationStatus.FAILED);
      notification.setState(new FailedState());
    } else if (this.sending(notification)) {
      notification.status(NotificationStatus.SENDING);
      notification.setState(new SendingState());
    }
  }
}
