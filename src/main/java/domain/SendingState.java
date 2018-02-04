package domain;

public class SendingState extends NotificationState {

	@Override
	public void changeStatus(Notification notification) {
		
		//valid transitions:
		//	-->	SENT.
		//	-->	FAILED.
		//	--> (NONE) SENDING.
		
		if(this.changeToFailedState(notification)) {
			notification.setStatus(NotificationStatus.FAILED);
			notification.setState(new FailedState());
		} else if(this.changeToSentState(notification)) {
			notification.setStatus(NotificationStatus.SENT);
			notification.setState(new SentState());
		}
		
		//change.
		notification.setState(this);
	}

}
