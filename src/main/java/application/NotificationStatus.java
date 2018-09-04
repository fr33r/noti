package application;

public enum NotificationStatus {

  /** Indicates that the notification has not yet been sent. */
  PENDING("PENDING"),

  /** Indicates that the notification has been sent. */
  SENT("SENT"),

  /** Indicates that the notification was cancelled prior to being sent. */
  CANCELLED("CANCELLED");

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
  @Override
  public String toString() {
    return this.status;
  }
}
