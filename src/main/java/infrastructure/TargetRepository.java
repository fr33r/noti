package infrastructure;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import domain.EntitySQLFactory;
import domain.Tag;
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
		final String tagSQL = 
			"SELECT T.* FROM TAG AS T INNER JOIN TARGET_TAG AS TT ON T.UUID = TT.TAG_UUID WHERE TT.TARGET_UUID = ?;";
		try(PreparedStatement pStatement = 
			this.getUnitOfWork().createPreparedStatement(targetSQL + tagSQL)){
			pStatement.setString(1, uuid.toString());
			pStatement.setString(2, uuid.toString());
			target = this.targetFactory.reconstitute(pStatement);
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
		
		//need to identify which tags are already associated to target.
		//perform a diff between the incoming list of tags and current list of tags.
		//add association to new tags. delete associations to missing tags.

		Target existingTarget = this.get(target.getId());
		if(existingTarget == null) {
			this.add(target);
		} else {
			final String getTagSQL = 
				"SELECT T.* FROM TAG AS T WHERE T.NAME = ?;";
			final String createTagSQL = 
				"INSERT INTO TAG (NAME) VALUES (?);";
			final String associateTagSQL = 
				"INSERT INTO TARGET_TAG (TARGET_UUID, TAG_UUID) VALUES (?, ?);";
			final String disassociateTagSQL = 
				"DELETE FROM TARGET_TAG WHERE TAG_UUID = ? AND TARGET_UUID = ?;";

			Set<Tag> tagsToAdd = new HashSet<>();
			Set<Tag> tagsToRemove = new HashSet<>();

			//determine which tags to remove.
			for(Tag tag : existingTarget.getTags()){
				if(!target.getTags().contains(tag)) {
					tagsToRemove.add(tag);
				}
			}

			//determine which tags to add.
			for(Tag tag : target.getTags()){
				if(!existingTarget.getTags().contains(tag)){
					tagsToAdd.add(tag);
				}
			}

			//remove tags.
			for(Tag tagToRemove : tagsToRemove){
				try(PreparedStatement pStatement = 
					this.getUnitOfWork().createPreparedStatement(disassociateTagSQL)){
					pStatement.setString(1, tagToRemove.getName());
					pStatement.setString(2, target.getId().toString());
					pStatement.executeUpdate();
				} catch (SQLException x) {
					throw new RuntimeException(x);
				}
			}

			//add tags.
			for(Tag tagToAdd : tagsToAdd){
				boolean isExistingTag = false;
				String existingTagUUID = null;

				//first see if the tag being added exists.
				try(PreparedStatement pStatement = 
					this.getUnitOfWork().createPreparedStatement(getTagSQL)){
					pStatement.setString(1, tagToAdd.getName());
					ResultSet results = pStatement.executeQuery();
					isExistingTag = results.next();
					existingTagUUID = results.getString("TAG_UUID");
				} catch (SQLException x) {
					throw new RuntimeException(x);
				}

				//if the tag being added doesn't exist...
				if(!isExistingTag){

					//create the tag.
					try(PreparedStatement pStatement = 
						this.getUnitOfWork().createPreparedStatement(createTagSQL)){
						pStatement.setString(1, tagToAdd.getName());
						pStatement.executeUpdate();
					} catch (SQLException x) {
						throw new RuntimeException(x);
					}

					//retrieve the tag just created.
					try(PreparedStatement pStatement = 
						this.getUnitOfWork().createPreparedStatement(getTagSQL)){
						pStatement.setString(1, tagToAdd.getName());
						ResultSet results = pStatement.executeQuery();
						isExistingTag = results.next();
						existingTagUUID = results.getString("TAG_UUID");
					} catch (SQLException x) {
						throw new RuntimeException(x);
					}
				}

				//associate the new tag to the target.
				try(PreparedStatement pStatement = 
					this.getUnitOfWork().createPreparedStatement(associateTagSQL)){
					pStatement.setString(1, target.getId().toString());
					pStatement.setString(2, existingTagUUID);
					pStatement.executeUpdate();
				} catch (SQLException x) {
					throw new RuntimeException(x);
				}
			}

			//update the target.
			try(PreparedStatement pStatement = 
				this.getUnitOfWork().createPreparedStatement(sql)) {
				pStatement.setString(1, target.getName());
				pStatement.setString(2, target.getPhoneNumber().toE164());
				pStatement.executeUpdate();
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
		//WHERE ARE UUIDs FOR NEW ENTITIES BEING CREATED?
		//SHOULD I SUPPORT SOFT DELETIONS FOR ENTITIES?

		final String createTargetSQL = 
			"INSERT INTO TARGET (UUID, NAME, PHONE_NUMBER) VALUES (?, ?, ?);";
		final String createTagSQL = 
			"INSERT INTO TAG (UUID, NAME) VALUES (?, ?);";
		final String associateTagSQL = 
			"INSERT INTO TARGET_TAG(TARGET_UUID, TAG_UUID) VALUES(?, ?);";
		final String getTagSQL = 
			"SELECT T.* FROM TAG AS T WHERE T.UUID = ?;";
		final String getTagByNameSQL = 
			"SELECT T.* FROM TAG AS T WHERE T.NAME = ?;";

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

		for(Tag tag : target.getTags()){
			boolean isExistingTag = false;
			String existingTagUUID = null;

			//check if tag already exists.
			try(PreparedStatement pStatement = 
				this.getUnitOfWork().createPreparedStatement(getTagByNameSQL)){
				pStatement.setString(1, tag.getName());
				ResultSet results = pStatement.executeQuery();
				if(isExistingTag = results.next()){
					existingTagUUID = results.getString("UUID");
				}
			} catch (SQLException x){
				throw new RuntimeException(x);
			}

			//if the tag being added doesn't exist...
			if(!isExistingTag){

				//create the tag.
				try(PreparedStatement pStatement = 
					this.getUnitOfWork().createPreparedStatement(createTagSQL)){
					pStatement.setString(1, tag.getName());
					pStatement.executeUpdate();
				} catch (SQLException x) {
					throw new RuntimeException(x);
				}

				//retrieve the tag just created.
				try(PreparedStatement pStatement = 
					this.getUnitOfWork().createPreparedStatement(getTagSQL)){
					pStatement.setString(1, tag.getName());
					ResultSet results = pStatement.executeQuery();
					isExistingTag = results.next();
					existingTagUUID = results.getString("UUID");
				} catch (SQLException x) {
					throw new RuntimeException(x);
				}
			}

			//associate the new tag to the target.
			try(PreparedStatement pStatement = 
				this.getUnitOfWork().createPreparedStatement(associateTagSQL)){
				pStatement.setString(1, target.getId().toString());
				pStatement.setString(2, existingTagUUID);
				pStatement.executeUpdate();
			} catch (SQLException x) {
				throw new RuntimeException(x);
			}
		}
	}

	/**
	 *	Removes the entity from the repository with the representation of the identity provided.
	 */
	@Override
	public void remove(UUID uuid) {
		final String sql = "DELETE FROM TARGET WHERE UUID = ?;";
		try(PreparedStatement pStatement = 
			this.getUnitOfWork().createPreparedStatement(sql)){
			pStatement.setString(1, uuid.toString());
			pStatement.executeUpdate();
		} catch (SQLException x) {
			throw new RuntimeException(x);
		}
	}
}
