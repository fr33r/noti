package application;

import java.util.UUID;

public class Template {

  private final UUID uuid;
  private final String content;

  public Template() {
    this(null, null);
  }

  public Template(final String content) {
    this(null, content);
  }

  public Template(final UUID uuid, final String content) {
    this.uuid = uuid;
    this.content = content;
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public String getContent() {
    return this.content;
  }
}
