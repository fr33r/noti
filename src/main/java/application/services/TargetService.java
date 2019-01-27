package application.services;

import application.InternalErrorException;
import application.NotFoundException;
import domain.Target;
import domain.TargetFactory;
import infrastructure.Repository;
import infrastructure.RepositoryFactory;
import infrastructure.UnitOfWork;
import infrastructure.UnitOfWorkFactory;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

public final class TargetService implements application.TargetService {

  private final UnitOfWorkFactory unitOfWorkFactory;
  private final RepositoryFactory repositoryFactory;
  private final TargetFactory targetFactory;
  private final application.TargetFactory applicationTargetFactory;
  private final Logger logger;

  @Inject
  public TargetService(
      UnitOfWorkFactory unitOfWorkFactory,
      RepositoryFactory repositoryFactory,
      TargetFactory targetFactory,
      application.TargetFactory applicationTargetFactory,
      @Named("application.services.TargetService") Logger logger) {
    this.unitOfWorkFactory = unitOfWorkFactory;
    this.repositoryFactory = repositoryFactory;
    this.targetFactory = targetFactory;
    this.applicationTargetFactory = applicationTargetFactory;
    this.logger = logger;
  }

  @Override
  public Integer getTargetCount() {

    try (UnitOfWork unitOfWork = this.unitOfWorkFactory.createUnitOfWork()) {
      Repository<Target, UUID> targetRepository =
          this.repositoryFactory.createTargetRepository(unitOfWork);
      return targetRepository.size();
    } catch (Exception x) {
      String errorMessage = "An error occurred when retrieving the total number of targets.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @param target {@inheritDoc}
   * @return {@inheritDoc}
   */
  @Override
  public UUID createTarget(application.Target target) {

    try (UnitOfWork unitOfWork = this.unitOfWorkFactory.createUnitOfWork()) {
      Target _target = this.targetFactory.createFrom(target);
      Repository<Target, UUID> targetRepository =
          this.repositoryFactory.createTargetRepository(unitOfWork);
      targetRepository.add(_target);
      return _target.getId();
    } catch (Exception x) {
      String errorMessage = "An error occurred when creating the target.";
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
  public application.Target getTarget(UUID uuid) {

    Target target = null;
    try (UnitOfWork unitOfWork = this.unitOfWorkFactory.createUnitOfWork()) {
      Repository<Target, UUID> targetRepository =
          this.repositoryFactory.createTargetRepository(unitOfWork);
      target = targetRepository.get(uuid);
    } catch (Exception x) {
      String errorMessage = "An error occurred when retrieving the target.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }

    if (target == null) {
      String errorMessage = "Can't find target.";
      String detailedMessage =
          String.format("Can't find target with UUID of '%s'", uuid.toString());
      this.logger.warn(detailedMessage);
      throw new NotFoundException(errorMessage, detailedMessage);
    }

    return this.applicationTargetFactory.createFrom(target);
  }

  /**
   * {@inheritDoc}
   *
   * @param target {@inheritDoc}
   */
  @Override
  public void replaceTarget(application.Target target) {

    try (UnitOfWork unitOfWork = this.unitOfWorkFactory.createUnitOfWork()) {
      Target _target = this.targetFactory.createFrom(target);
      Repository<Target, UUID> targetRepository =
          this.repositoryFactory.createTargetRepository(unitOfWork);
      targetRepository.put(_target);
    } catch (Exception x) {
      String errorMessage = "An error occurred when updating the target.";
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
  public void deleteTarget(UUID uuid) {

    try (UnitOfWork unitOfWork = this.unitOfWorkFactory.createUnitOfWork()) {
      Repository<Target, UUID> targetRepository =
          this.repositoryFactory.createTargetRepository(unitOfWork);
      targetRepository.remove(uuid);
    } catch (Exception x) {
      String errorMessage = "An error occurred when deleting the target.";
      this.logger.error(errorMessage, x);
      throw new InternalErrorException(errorMessage, x.getMessage());
    }
  }
}
