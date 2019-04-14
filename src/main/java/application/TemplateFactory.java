package application;

public class TemplateFactory {

  public Template createFrom(api.representations.xml.Template template) {
    return new Template(template.getUUID(), template.getContent());
  }

  public Template createFrom(api.representations.json.Template template) {
    return new Template(template.getUUID(), template.getContent());
  }

  public Template createFrom(domain.Template template) {
    return new Template(template.getId(), template.getContent());
  }
}
