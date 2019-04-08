package domain;

public class TemplateParameter extends ValueObject {

  private final String name;
  private final Object value;

  public TemplateParameter(final String name, final Object value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return this.name;
  }

  public Object getValue() {
    return this.value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass()) return false;

    TemplateParameter param = (TemplateParameter) obj;

    boolean sameName =
        this.getName() == null && param.getName() == null
            || (this.getName() != null
                && param.getName() != null
                && this.getName().equalsIgnoreCase(param.getName()));
    boolean sameValue =
        this.getValue() == null && param.getValue() == null
            || (this.getValue() != null
                && param.getValue() != null
                && this.getValue().equals(param.getValue()));

    return sameName && sameValue;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    final int prime = 17;

    hashCode = hashCode * prime + this.getName().hashCode();

    if (this.getValue() != null) {
      hashCode = hashCode * prime + this.getValue().hashCode();
    }

    return hashCode;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder
        .append("[")
        .append("name=")
        .append(this.getName())
        .append(", ")
        .append("value=")
        .append(this.getValue() == null ? "null" : this.getValue().toString())
        .append("]");
    return builder.toString();
  }
}
