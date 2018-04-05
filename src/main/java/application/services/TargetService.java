package application.services;

import java.util.UUID;
import javax.inject.Inject;

import domain.Target;
import domain.TargetFactory;
import infrastructure.SQLUnitOfWorkFactory;
import infrastructure.SQLUnitOfWork;
import infrastructure.RepositoryFactory;
import infrastructure.Repository;

public final class TargetService implements application.TargetService {

	private final SQLUnitOfWorkFactory unitOfWorkFactory;
	private final RepositoryFactory repositoryFactory;
	private final TargetFactory targetFactory;
	private final application.TargetFactory applicationTargetFactory;

	@Inject
	public TargetService(
		SQLUnitOfWorkFactory unitOfWorkFactory,
		RepositoryFactory repositoryFactory,
		TargetFactory targetFactory,
		application.TargetFactory applicationTargetFactory
	) {
		this.unitOfWorkFactory = unitOfWorkFactory;
		this.repositoryFactory = repositoryFactory;
		this.targetFactory = targetFactory;
		this.applicationTargetFactory = applicationTargetFactory;
	}

	@Override
	public UUID createTarget(application.Target target) {

		Target target_domain = this.targetFactory.createFrom(target);
		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

		try {
			Repository<Target, UUID> targetRepository =
				this.repositoryFactory.createTargetRepository(unitOfWork);
			targetRepository.add(target_domain);
			unitOfWork.save();
			return target_domain.getId();
		} catch (Exception x){
			//log.
			unitOfWork.undo();
			throw x;
		}
	}

	@Override
	public application.Target getTarget(UUID uuid) {

		Target target_domain = null;
		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

		try {
			Repository<Target, UUID> targetRepository =
				this.repositoryFactory.createTargetRepository(unitOfWork);
			target_domain = targetRepository.get(uuid);
			unitOfWork.save();
		} catch (Exception x) {
			//log.
			unitOfWork.undo();
			throw x;
		}

		if (target_domain == null) {
			throw new RuntimeException(String.format("Can't find target with UUID of '%s'", uuid.toString()));
		}

		return this.applicationTargetFactory.createFrom(target_domain);
	}

	@Override
	public void replaceTarget(application.Target target) {

		Target target_domain = this.targetFactory.createFrom(target);
		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

		try {
			Repository<Target, UUID> targetRepository = 
				this.repositoryFactory.createTargetRepository(unitOfWork);
			targetRepository.put(target_domain);
			unitOfWork.save();
		} catch (Exception x) {
			//log.
			unitOfWork.undo();
			throw x;
		}
	}

	@Override
	public void deleteTarget(UUID uuid) {

		SQLUnitOfWork unitOfWork = this.unitOfWorkFactory.create();

		try {
			Repository<Target, UUID> targetRepository = 
				this.repositoryFactory.createTargetRepository(unitOfWork);
			targetRepository.remove(uuid);
			unitOfWork.save();
		} catch (Exception x) {
			//log.
			unitOfWork.undo();
			throw x;
		}
	}
}
