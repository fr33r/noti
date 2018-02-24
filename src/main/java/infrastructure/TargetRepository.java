package infrastructure;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import domain.EntitySQLFactory;
import domain.Target;

/**
 * Responsible for retrieving and persisting {@link Target} entities.
 * @author jonfreer
 *
 */
public final class TargetRepository extends SQLRepository implements Repository<Target, UUID> {

	private final EntitySQLFactory<Target, UUID> targetFactory;

	/**
	 * Constructs a instance of {@link TargetRepository}.
	 * @param unitOfWork The unit of work that this repository will contribute to.
	 * @param targetFactory The factory that reconstitutes {@link Target} entities.
	 */
	public TargetRepository(
		SQLUnitOfWork unitOfWork,
		EntitySQLFactory<Target, UUID> targetFactory
	) {
		super(unitOfWork);
		this.targetFactory = targetFactory;
	}

	/**
	 *	Retrieves the entity from the repository by a representation of the entity's identity.
	 */
	@Override
	public Target get(UUID uuid) {
		Target target = null;
		final String targetSQL = 
			"SELECT T.* FROM TARGET AS T WHERE T.UUID = ?;";
		try(
			PreparedStatement getTargetStatement = 
				this.getUnitOfWork().createPreparedStatement(targetSQL)
		){
			getTargetStatement.setString(1, uuid.toString());

			try(ResultSet targetRs = getTargetStatement.executeQuery()) {
				target = this.targetFactory.reconstitute(targetRs);
			}
		} catch (SQLException x) {
			throw new RuntimeException(x);
		}
		return target;
	}

	/**
	 *	Places the entity provided into the repository. If the entity provided already exists
	 *	in the repository, it's state is replaced with the state provided.
	 */
	@Override
	public void put(Target target) {
		final String sql = 
			"UPDATE TARGET SET NAME = ?, PHONE_NUMBER = ? WHERE UUID = ?;";
		
		Target existingTarget = this.get(target.getId());
		if(existingTarget == null) {
			this.add(target);
		} else {

			//update the target.
			try(
				PreparedStatement updateTargetStatement =
					this.getUnitOfWork().createPreparedStatement(sql)
			) {
				updateTargetStatement.setString(1, target.getName());
				updateTargetStatement.setString(2, target.getPhoneNumber().toE164());
				updateTargetStatement.setString(3, target.getId().toString());
				updateTargetStatement.executeUpdate();
			} catch (SQLException x) {
				throw new RuntimeException(x);
			}
		}
	}

	/**
	 *	Inserts the entity provided into the repository. 
	 */
	@Override
	public void add(Target target) {
		//SHOULD I SUPPORT SOFT DELETIONS FOR ENTITIES?

		final String createTargetSQL = 
			"INSERT INTO TARGET (UUID, NAME, PHONE_NUMBER) VALUES (?, ?, ?);";

		//create target.
		try(PreparedStatement pStatement = 
			this.getUnitOfWork().createPreparedStatement(createTargetSQL)) {
			pStatement.setString(1, target.getId().toString());
			pStatement.setString(2, target.getName());
			pStatement.setString(3, target.getPhoneNumber().toE164());
			pStatement.executeUpdate();
		} catch (SQLException x) {
			throw new RuntimeException(x);
		}
	}

	/**
	 *	Removes the entity from the repository with the representation of the identity provided.
	 */
	@Override
	public void remove(UUID uuid) {
		final String deleteTargetSQL = "DELETE FROM TARGET WHERE UUID = ?;";
		final String deleteNotificationsAssociationSQL =
			"DELETE FROM NOTIFICATION_TARGET WHERE TARGET_UUID = ?;";
		try(
			PreparedStatement deleteNotificationAssociationsStatement =
				this.getUnitOfWork().createPreparedStatement(deleteNotificationsAssociationSQL);
			PreparedStatement deleteTargetStatement =
				this.getUnitOfWork().createPreparedStatement(deleteTargetSQL)
		){
			deleteNotificationAssociationsStatement.setString(1, uuid.toString());
			deleteNotificationAssociationsStatement.executeUpdate();

			deleteTargetStatement.setString(1, uuid.toString());
			deleteTargetStatement.executeUpdate();
		} catch (SQLException x) {
			throw new RuntimeException(x);
		}
	}
}
