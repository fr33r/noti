package domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

@Service
@Named("TargetSQLFactory")
public class TargetSQLFactory extends EntitySQLFactory<Target, UUID> {

	private static final String uuidColumn			= "uuid";
	private static final String phoneNumberColumn	= "phone_number";
	private static final String nameColumn			= "name";

	@Inject
	public TargetSQLFactory(){}

	public Target reconstitute(Statement statement) {
		if(statement == null) { return null; }
		
		Target target = null;
		try{
			if(statement.isClosed()) { return null; }
			target = this.extractTarget(statement.getResultSet());
		} catch (SQLException x) {
			throw new RuntimeException(x);
		}
		return target;
	}

	private Target extractTarget(ResultSet results) throws SQLException {
		if(results.next()){
			String uuid = results.getString(uuidColumn);
			String name = results.getString(nameColumn);
			String phoneNumber = results.getString(phoneNumberColumn);
			return new Target(UUID.fromString(uuid), name, new PhoneNumber(phoneNumber));
		}
		return null;
	}

	@Override
	public Target reconstitute(ResultSet... results) {
		if(results == null || results.length < 1) { return null; }
		
		Target target = null;
		try{
			target = this.extractTarget(results[0]);
		} catch (SQLException x) {
			throw new RuntimeException(x);
		}
		return target;
	}
}
