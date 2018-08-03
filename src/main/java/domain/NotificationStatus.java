package domain;

public enum NotificationStatus {

  /** Indicates that the notification has not yet been sent. */
  PENDING("PENDING"),

  /** Indicates that the notification in currently being sent. */
  SENDING("SENDING"),

  /** Indicates that the notification has been sent. */
  SENT("SENT"),

  /** Indicates that the notification was cancelled prior to being sent. */
  CANCELLED("CANCELLED"),

  /** Indicates that the notification did not send to anyone. */
  FAILED("FAILED");

  private String status;

  /**
   * Constructs the {@link NotificationStatus} enum provided the textual representation of the
   * notification status.
   *
   * @param status The textual representation of the notification status.
   */
  NotificationStatus(String status) {
    this.status = status;
  }

  /**
   * Retrieves a textual representation of the notification status.
   *
   * @return A textual representation of the notification status.
   */
  public String toString() {
    return this.status;
  }
}
