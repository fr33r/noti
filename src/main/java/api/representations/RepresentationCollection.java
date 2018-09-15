package api.representations;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "collection")
public final class RepresentationCollection extends Representation {

  private int total;
  private Set<Representation> representations;

  public RepresentationCollection() {
    super();
    this.total = 0;
    this.representations = new HashSet<>();
  }

  public RepresentationCollection(MediaType mediaType) {
    super(mediaType);
    this.total = 0;
    this.representations = new HashSet<>();
  }

  public static class Builder extends Representation.Builder {

    private int total;
    private Set<Representation> representations;

    public Builder(MediaType mediaType) {
      super(mediaType);
      this.total = 0;
      this.representations = new HashSet<>();
    }

    public Builder add(Representation representation) {
      this.representations.add(representation);
      return this;
    }

    public Builder total(int total) {
      this.total = total;
      return this;
    }

    @Override
    public Representation build() {
      RepresentationCollection rc = new RepresentationCollection(this.mediaType());
      rc.setLocation(this.location());
      rc.setEncoding(this.encoding());
      rc.setLanguage(this.language());
      rc.setLastModified(this.lastModified());
      rc.setElements(this.representations);
      rc.setTotal(this.total);
      return rc;
    }
  }

  @XmlElementWrapper(name = "elements")
  @XmlElement(name = "element")
  public Set<Representation> getElements() {
    return this.representations;
  }

  private void setElements(Set<Representation> representations) {
    this.representations = representations;
  }

  @XmlElement(name = "total")
  public int getTotal() {
    return this.total;
  }

  private void setTotal(int total) {
    this.total = total;
  }
}
