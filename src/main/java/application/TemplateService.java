package application;

import java.util.UUID;

/**
 * Defines the abstraction that exposes various application operations for templates.
 *
 * @author Jon Freer
 */
public interface TemplateService {

  /**
   * Creates a new {@link application.Template}.
   *
   * @param template The state of the {@link application.Template} to create.
   * @return The universally unique identifer of the newly created {@link application.Template}.
   */
  UUID createTemplate(Template template);

  /**
   * Retrieves an existing {@link application.Template}.
   *
   * @param uuid The universally unique identifer of the {@link application.Template} being
   *     retrieved.
   * @return The {@link application.Template} with the universally unique identifer provided.
   */
  Template getTemplate(UUID uuid);

  /**
   * Replaces the current state of the {@link application.Template} with the state provided.
   *
   * @param template The desired state of the {@link application.Template}.
   */
  void replaceTemplate(Template template);

  /**
   * Deletes an existing {@link application.Template}.
   *
   * @param uuid The universally unique identifier of the {@link application.Template} to delete.
   */
  void deleteTemplate(UUID uuid);

  Integer getTemplateCount();
}
