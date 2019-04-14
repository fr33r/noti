package application.services;

import application.InternalErrorException;
import application.NotFoundException;
import domain.Template;
import domain.TemplateFactory;
import infrastructure.Repository;
import infrastructure.RepositoryFactory;
import infrastructure.UnitOfWork;
import infrastructure.UnitOfWorkFactory;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

public final class TemplateService implements application.TemplateService {

  private final UnitOfWorkFactory unitOfWorkFactory;
  private final RepositoryFactory repositoryFactory;
  private final TemplateFactory templateFactory;
  private final application.TemplateFactory applicationTemplateFactory;
  private final Logger logger;

  @Inject
  public TemplateService(
      UnitOfWorkFactory unitOfWorkFactory,
      RepositoryFactory repositoryFactory,
      TemplateFactory templateFactory,
      application.TemplateFactory applicationTemplateFactory,
      @Named("application.services.TemplateService") Logger logger) {
    this.unitOfWorkFactory = unitOfWorkFactory;
    this.repositoryFactory = repositoryFactory;
    this.templateFactory = templateFactory;
    this.applicationTemplateFactory = applicationTemplateFactory;
    this.logger = logger;
  }

  @Override
  public Integer getTemplateCount() {

    try (UnitOfWork unitOfWork = this.unitOfWorkFactory.createUnitOfWork()) {
      Repository<Template, UUID> templateRepository =
          this.repositoryFactory.createTemplateRepository(unitOfWork);
      return templateRepository.size();
    } catch (Exception x) {
      String errorMessage = "An error occurred when retrieving the total number of templates.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param template {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public UUID createTemplate(application.Template template) {

    try (UnitOfWork unitOfWork = this.unitOfWorkFactory.createUnitOfWork()) {
      Template _template = this.templateFactory.createFrom(template);
      Repository<Template, UUID> templateRepository =
          this.repositoryFactory.createTemplateRepository(unitOfWork);
      templateRepository.add(_template);
      return _template.getId();
    } catch (Exception x) {
      String errorMessage = "An error occurred when creating the template.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param uuid {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public application.Template getTemplate(UUID uuid) {

    Template template = null;
    try (UnitOfWork unitOfWork = this.unitOfWorkFactory.createUnitOfWork()) {
      Repository<Template, UUID> templateRepository =
          this.repositoryFactory.createTemplateRepository(unitOfWork);
      template = templateRepository.get(uuid);
    } catch (Exception x) {
      String errorMessage = "An error occurred when retrieving the template.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }

    if (template == null) {
      String errorMessage = "Can't find template.";
      String detailedMessage =
          String.format("Can't find template with UUID of '%s'", uuid.toString());
      this.logger.warn(detailedMessage);
      throw new NotFoundException(errorMessage, detailedMessage);
    }

    return this.applicationTemplateFactory.createFrom(template);
  }

  /**
   * {@inheritDoc}
   *
   * @param template {@inheritDoc}
   */
  @Override
  public void replaceTemplate(application.Template template) {

    try (UnitOfWork unitOfWork = this.unitOfWorkFactory.createUnitOfWork()) {
      Template _template = this.templateFactory.createFrom(template);
      Repository<Template, UUID> templateRepository =
          this.repositoryFactory.createTemplateRepository(unitOfWork);
      templateRepository.put(_template);
    } catch (Exception x) {
      String errorMessage = "An error occurred when updating the template.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param uuid {@inheritDoc}
   */
  @Override
  public void deleteTemplate(UUID uuid) {

    try (UnitOfWork unitOfWork = this.unitOfWorkFactory.createUnitOfWork()) {
      Repository<Template, UUID> templateRepository =
          this.repositoryFactory.createTemplateRepository(unitOfWork);
      templateRepository.remove(uuid);
    } catch (Exception x) {
      String errorMessage = "An error occurred when deleting the template.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }
  }
}
