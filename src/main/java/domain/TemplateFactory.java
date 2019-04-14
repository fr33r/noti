package domain;

import java.util.UUID;

// temporary solution for now. need to look more into
// factories with DDD. not a fan of everything being a concrete type.
public class TemplateFactory {

  public Template createFrom(application.Template template) {
    UUID uuid = template.getUUID() == null ? UUID.randomUUID() : template.getUUID();
    return new Template(uuid, template.getContent());
  }
}
