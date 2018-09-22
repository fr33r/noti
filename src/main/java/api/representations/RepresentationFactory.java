package api.representations;

import application.ApplicationException;
import application.Audience;
import application.Message;
import application.Notification;
import application.Target;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import javax.ws.rs.core.MediaType;

/**
 * A factory of all {@link Representation} instances.
 *
 * @author Jon Freer
 */
public abstract class RepresentationFactory {

  private final MediaType mediaType;

  /**
   * Constructs a new {@link RepresentationFactory}.
   *
   * @param mediaType The media type of the representations that the factory produces.
   */
  public RepresentationFactory(MediaType mediaType) {
    this.mediaType = mediaType;
  }

  /**
   * Retrieves the media type of the representations this factory produces.
   *
   * @return The media type of the representations that this factory produces.
   */
  public MediaType getMediaType() {
    return this.mediaType;
  }

  // TODO - encapsulate the parameters of these methods within
  // RepresentationMetadata instances to reduce the risk of
  // breaking signature changes.

  /**
   * Constructs a notification representation.
   *
   * @param location The content location of the notification representation.
   * @param language The content language of the notification representation.
   * @param notification The notification state expressed by the notification representation being
   *     constructed.
   * @return The notification representation.
   */
  public abstract Representation createNotificationRepresentation(
      URI location, Locale language, Notification notification);

  /**
   * Constructs an audience representation.
   *
   * @param location The content location of the audience representation.
   * @param language The content language of the audience representation.
   * @param audience The audience state expressed by the audience representation being constructed.
   * @return The audience representation.
   */
  public abstract Representation createAudienceRepresentation(
      URI location, Locale language, Audience audience);

  /**
   * Constructs an target representation.
   *
   * @param location The content location of the target representation.
   * @param language The content language of the target representation.
   * @param target The target state expressed by the target representation being constructed.
   * @return The target representation.
   */
  public abstract Representation createTargetRepresentation(
      URI location, Locale language, Target target);

  public abstract Representation createMessageRepresentation(
      URI location, Locale language, Message message);

  /**
   * Constructs a notification collection representation.
   *
   * @param location The content location of the notification collection representation.
   * @param language The content language of the notification collection representation.
   * @param notifications The notification collection state expressed by the notification collection
   *     representation being constructed.
   * @param skip The number of notifications skipped (in previous pages).
   * @param take The maximum number of notifications in the current page of the collection.
   * @param total The total number of notifications in the collection (not the current page).
   * @return The notification collection representation.
   */
  public abstract Representation createNotificationCollectionRepresentation(
      URI location,
      Locale language,
      Set<Notification> notifications,
      Integer skip,
      Integer take,
      Integer total);

  /**
   * Constructs a target collection representation.
   *
   * @param location The content location of the target collection representation.
   * @param language The content language of the target collection representation.
   * @param targets The target collection state expressed by the target collection representation
   *     being constructed.
   * @param skip The number of targets skipped (in previous pages).
   * @param take The maximum number of targets in the current page of the collection.
   * @param total The total number of target in the collection (not the current page).
   * @return The target collection representation.
   */
  public abstract Representation createTargetCollectionRepresentation(
      URI location,
      Locale language,
      Set<Target> targets,
      Integer skip,
      Integer take,
      Integer total);

  /**
   * Constructs a audience collection representation.
   *
   * @param location The content location of the audience collection representation.
   * @param language The content language of the audience collection representation.
   * @param audiences The audience collection state expressed by the audience collection
   *     representation being constructed.
   * @param skip The number of audiences skipped (in previous pages).
   * @param take The maximum number of audiences in the current page of the collection.
   * @param total The total number of audience in the collection (not the current page).
   * @return The audience collection representation.
   */
  public abstract Representation createAudienceCollectionRepresentation(
      URI location,
      Locale language,
      Set<Audience> audiences,
      Integer skip,
      Integer take,
      Integer total);

  /**
   * Constructs a message collection representation.
   *
   * @param location The content location of the message collection representation.
   * @param language The content language of the message collection representation.
   * @param messages The message collection state expressed by the message collection representation
   *     being constructed.
   * @param skip The number of messages skipped (in previous pages).
   * @param take The maximum number of messages in the current page of the collection.
   * @param total The total number of message in the collection (not the current page).
   * @return The message collection representation.
   */
  public abstract Representation createMessageCollectionRepresentation(
      URI location,
      Locale language,
      Set<Message> messages,
      Integer skip,
      Integer take,
      Integer total);

  public abstract Representation createErrorRepresentation(
      URI location, Locale language, ApplicationException exception);
}
