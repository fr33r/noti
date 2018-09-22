package application.services;

import domain.Audience;
import domain.AudienceFactory;
import domain.Target;
import infrastructure.Repository;
import infrastructure.RepositoryFactory;
import infrastructure.SQLUnitOfWork;
import infrastructure.SQLUnitOfWorkFactory;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

public final class AudienceService implements application.AudienceService {

  private final SQLUnitOfWorkFactory unitOfWorkFactory;
  private final RepositoryFactory repositoryFactory;
  private final AudienceFactory audienceFactory;
  private final application.AudienceFactory applicationAudienceFactory;
  private final Logger logger;

  @Inject
  public AudienceService(
      SQLUnitOfWorkFactory unitOfWorkFactory,
      RepositoryFactory repositoryFactory,
      AudienceFactory audienceFactory,
      application.AudienceFactory applicationAudienceFactory,
      @Named("application.services.AudienceService") Logger logger) {
    this.unitOfWorkFactory = unitOfWorkFactory;
    this.repositoryFactory = repositoryFactory;
    this.audienceFactory = audienceFactory;
    this.applicationAudienceFactory = applicationAudienceFactory;
    this.logger = logger;
  }

  @Override
  public UUID createAudience(final application.Audience audience) {

    Audience _audience = this.audienceFactory.createFrom(audience);
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

    try {
      Repository<Audience, UUID> audienceRepository =
          this.repositoryFactory.createAudienceRepository(unitOfWork);
      audienceRepository.put(_audience);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      this.logger.error("An error occurred when creating the audience.", x);
      throw new RuntimeException(x);
    }
    return _audience.getId();
  }

  @Override
  public application.Audience getAudience(final UUID uuid) {

    Audience audience = null;
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

    try {
      Repository<Audience, UUID> audienceRepository =
          this.repositoryFactory.createAudienceRepository(unitOfWork);
      audience = audienceRepository.get(uuid);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      this.logger.error("An error occurred when retrieving the audience.", x);
      throw new RuntimeException(x);
    }

    if (audience == null) {
      this.logger.warn("Couldn't find a audience with UUID '{}'.", uuid.toString());
      throw new RuntimeException(
          String.format("Can't find audience with UUID of '%s'", uuid.toString()));
    }

    return this.applicationAudienceFactory.createFrom(audience);
  }

  @Override
  public void replaceAudience(final application.Audience audience) {

    Audience _audience = this.audienceFactory.createFrom(audience);
    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

    try {
      Repository<Audience, UUID> audienceRepository =
          this.repositoryFactory.createAudienceRepository(unitOfWork);
      audienceRepository.put(_audience);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      this.logger.error("An error occurred when updating the audience.", x);
      throw new RuntimeException(x);
    }
  }

  @Override
  public void deleteAudience(final UUID uuid) {

    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

    try {
      Repository<Audience, UUID> audienceRepository =
          this.repositoryFactory.createAudienceRepository(unitOfWork);
      audienceRepository.remove(uuid);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      this.logger.error("An error occurred when deleting the audience.", x);
      throw new RuntimeException(x);
    }
  }

  @Override
  public void associateMemberToAudience(final UUID audienceUUID, final UUID memberUUID) {

    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

    try {
      Repository<Audience, UUID> audienceRepository =
          this.repositoryFactory.createAudienceRepository(unitOfWork);
      Repository<Target, UUID> targetRepository =
          this.repositoryFactory.createTargetRepository(unitOfWork);
      Target target = targetRepository.get(memberUUID);

      if (target == null) {
        this.logger.warn("Couldn't find a member with UUID '{}'.", memberUUID.toString());
        throw new RuntimeException(
            String.format("Can't find target with UUID of '%s'.", memberUUID.toString()));
      }

      Audience audience = audienceRepository.get(audienceUUID);

      if (audience == null) {
        this.logger.warn("Couldn't find a audience with UUID '{}'.", audienceUUID.toString());
        throw new RuntimeException(
            String.format("Can't find audience with UUID of '%s'.", audienceUUID.toString()));
      }

      audience.include(target);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      this.logger.error("An error occurred when associating the member to the audience.", x);
      throw new RuntimeException(x);
    }
  }

  @Override
  public void disassociateMemberFromAudience(final UUID audienceUUID, final UUID memberUUID) {

    SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

    try {
      Repository<Audience, UUID> audienceRepository =
          this.repositoryFactory.createAudienceRepository(unitOfWork);
      Repository<Target, UUID> targetRepository =
          this.repositoryFactory.createTargetRepository(unitOfWork);
      Target target = targetRepository.get(memberUUID);

      if (target == null) {
        this.logger.warn("Couldn't find a member with UUID '{}'.", memberUUID.toString());
        throw new RuntimeException(
            String.format("Can't find member with UUID of '%s'.", memberUUID.toString()));
      }

      Audience audience = audienceRepository.get(audienceUUID);

      if (audience == null) {
        this.logger.warn("Couldn't find a audience with UUID '{}'.", audienceUUID.toString());
        throw new RuntimeException(
            String.format("Can't find audience with UUID of '%s'.", audienceUUID.toString()));
      }

      audience.remove(target);
      unitOfWork.save();
    } catch (Exception x) {
      unitOfWork.undo();
      this.logger.error("An error occurred when disassociating the member to the audience.", x);
      throw new RuntimeException(x);
    }
  }
}
