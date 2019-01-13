package domain;

public class SendingState extends NotificationState {

  @Override
  public void next(Notification notification) {

    // valid transitions:
    //	-->	SENT.
    //	-->	FAILED.
    //	--> (NONE) SENDING.

    if (this.failed(notification)) {
      notification.status(NotificationStatus.FAILED);
      notification.setState(new FailedState());
    } else if (this.sent(notification)) {
      notification.status(NotificationStatus.SENT);
      notification.setState(new SentState());
    }
  }
}
