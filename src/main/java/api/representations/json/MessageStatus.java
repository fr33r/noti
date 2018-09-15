package api.representations.json;

public enum MessageStatus {
  PENDING("PENDING"),

  SENT("SENT"),

  DELIVERED("DELIVERED"),

  FAILED("FAILED");

  private String status;

  private MessageStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return this.status;
  }
}
