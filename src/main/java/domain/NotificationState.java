package domain;

public abstract class NotificationState {
  abstract void changeStatus(final Notification notification);

  boolean changeToFailedState(final Notification notification) {
    final int TOTAL_MESSAGE_COUNT = notification.messages().size();
    int failedCount = 0;

    for (Message message : notification.messages()) {
      if (message.getStatus() == MessageStatus.FAILED) {
        failedCount++;
      }
    }
    return failedCount == TOTAL_MESSAGE_COUNT;
  }

  boolean changeToSentState(final Notification notification) {
    final int TOTAL_MESSAGE_COUNT = notification.messages().size();

    int sentCount = 0, deliveredCount = 0;
    for (Message message : notification.messages()) {
      if (message.getStatus() == MessageStatus.SENT) {
        sentCount++;
      } else if (message.getStatus() == MessageStatus.DELIVERED) {
        deliveredCount++;
      }
    }

    return sentCount + deliveredCount == TOTAL_MESSAGE_COUNT;
  }

  boolean changeToSendingState(final Notification notification) {
    final int TOTAL_MESSAGE_COUNT = notification.messages().size();

    int sentCount = 0, deliveredCount = 0, pendingCount = 0;
    for (Message message : notification.messages()) {
      if (message.getStatus() == MessageStatus.SENT) {
        sentCount++;
      } else if (message.getStatus() == MessageStatus.DELIVERED) {
        deliveredCount++;
      } else if (message.getStatus() == MessageStatus.PENDING) {
        pendingCount++;
      }
    }

    return (sentCount + deliveredCount != TOTAL_MESSAGE_COUNT)
        && (pendingCount != TOTAL_MESSAGE_COUNT);
  }
}
