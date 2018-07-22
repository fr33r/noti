package api.representations;


import application.Audience;
import application.Notification;
import application.Target;
import java.net.URI;
import java.util.Locale;
import javax.ws.rs.core.MediaType;

public abstract class RepresentationFactory {

  private final MediaType mediaType;

  public RepresentationFactory(MediaType mediaType) {
    this.mediaType = mediaType;
  }

  protected MediaType getMediaType() {
    return this.mediaType;
  }

  public abstract Representation createNotificationRepresentation(
      URI location, Locale language, Notification notification);

  public abstract Representation createAudienceRepresentation(
      URI location, Locale language, Audience audience);

  public abstract Representation createTargetRepresentation(
      URI location, Locale language, Target target);
}
