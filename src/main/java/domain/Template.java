package domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.text.StringSubstitutor;

public class Template extends Entity<UUID> implements Cloneable {

  private String content;

  public Template(String content) {
    super();
    this.content = content;
  }

  public Template(UUID uuid, String content) {
    super(uuid);
    this.content = content;
  }

  public Template(Template template) {
    super(template.getId());
    this.content = template.getContent();
  }

  @Override
  public boolean isAggregateRoot() {
    return true;
  }

  public String getContent() {
    return this.content;
  }

  public String resolve(List<TemplateParameter> parameters) {
    Map<String, Object> variables = new HashMap<>();
    for (TemplateParameter parameter : parameters) {
      variables.put(parameter.getName(), parameter.getValue());
    }
    StringSubstitutor substitutor = new StringSubstitutor(variables);
    return substitutor.replace(this.getContent());
  }

  @Override
  public Object clone() {

    Template template = null;

    try {
      template = (Template) super.clone();
    } catch (CloneNotSupportedException ex) {
      // not possible;
    }

    return template;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder
        .append("[")
        .append("uuid=")
        .append(this.getId())
        .append(", ")
        .append("content=")
        .append(this.getContent())
        .append("]");
    return builder.toString();
  }
}
