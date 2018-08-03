package domain;

public class SendingState extends NotificationState {

  @Override
  public void changeStatus(Notification notification) {

    // valid transitions:
    //	-->	SENT.
    //	-->	FAILED.
    //	--> (NONE) SENDING.

    if (this.changeToFailedState(notification)) {
      notification.status(NotificationStatus.FAILED);
      notification.setState(new FailedState());
    } else if (this.changeToSentState(notification)) {
      notification.status(NotificationStatus.SENT);
      notification.setState(new SentState());
    }

    // change.
    notification.setState(this);
  }
}
