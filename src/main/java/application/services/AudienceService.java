package application.services;

import java.util.UUID;

import javax.inject.Inject;

import domain.Audience;
import domain.AudienceFactory;
import domain.Target;
import infrastructure.SQLUnitOfWorkFactory;
import infrastructure.SQLUnitOfWork;
import infrastructure.RepositoryFactory;
import infrastructure.Repository;

public final class AudienceService implements application.AudienceService {

	private final SQLUnitOfWorkFactory unitOfWorkFactory;
	private final RepositoryFactory repositoryFactory;
	private final AudienceFactory audienceFactory;
	private final application.AudienceFactory applicationAudienceFactory;

	@Inject
	public AudienceService(
		SQLUnitOfWorkFactory unitOfWorkFactory,
		RepositoryFactory repositoryFactory,
		AudienceFactory audienceFactory,
		application.AudienceFactory applicationAudienceFactory
	) {
		this.unitOfWorkFactory = unitOfWorkFactory;
		this.repositoryFactory = repositoryFactory;
		this.audienceFactory = audienceFactory;
		this.applicationAudienceFactory = applicationAudienceFactory;
	}

	@Override
	public UUID createAudience(final application.Audience audience) {

		Audience audience_domain = this.audienceFactory.createFrom(audience);
		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

		try {
			Repository<Audience, UUID> audienceRepository =
				this.repositoryFactory.createAudienceRepository(unitOfWork);
			audienceRepository.put(audience_domain);
			unitOfWork.save();
		} catch (Exception x) {
			//log.
			unitOfWork.undo();
			throw x;
		}
		return audience_domain.getId();
	}

	@Override
	public application.Audience getAudience(final UUID uuid) {

		Audience audience_domain = null;
		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

		try {
			Repository<Audience, UUID> audienceRepository =
				this.repositoryFactory.createAudienceRepository(unitOfWork);
			audience_domain = audienceRepository.get(uuid);
			unitOfWork.save();
		} catch (Exception x) {
			//log.
			unitOfWork.undo();
			throw x;
		}

		if(audience_domain == null) {
			throw new RuntimeException(
				String.format("Can't find audience with UUID of '%s'", uuid.toString())
			);
		}

		return this.applicationAudienceFactory.createFrom(audience_domain);
	}

	@Override
	public void replaceAudience(final application.Audience audience) {

		Audience audience_domain = this.audienceFactory.createFrom(audience);
		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

		try {
			Repository<Audience, UUID> audienceRepository =
				this.repositoryFactory.createAudienceRepository(unitOfWork);
			audienceRepository.put(audience_domain);
			unitOfWork.save();
		} catch (Exception x) {
			//log.
			unitOfWork.undo();
			throw x;
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
			//log.
			unitOfWork.undo();
			throw x;
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

			if(target == null) {
				throw new RuntimeException(
					String.format("Can't find target with UUID of '%s'.", memberUUID.toString())
				);
			}

			Audience audience = audienceRepository.get(audienceUUID);

			if(audience == null) {
				throw new RuntimeException(
					String.format("Can't find audience with UUID of '%s'.", audienceUUID.toString())
				);
			}

			audience.include(target);
			unitOfWork.save();
		} catch (Exception x) {
			//log.
			unitOfWork.undo();
			throw x;
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

			if(target == null) {
				throw new RuntimeException(
					String.format("Can't find target with UUID of '%s'.", memberUUID.toString())
				);
			}

			Audience audience = audienceRepository.get(audienceUUID);

			if(audience == null) {
				throw new RuntimeException(
					String.format("Can't find audience with UUID of '%s'.", audienceUUID.toString())
				);
			}

			audience.remove(target);
			unitOfWork.save();
		} catch (Exception x) {
			//log.
			unitOfWork.undo();
			throw x;
		}
	}
}
