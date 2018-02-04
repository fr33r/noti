package infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.jvnet.hk2.annotations.Service;

/**
 * Factory that creates MySQLUnitOfWork instances.
 */
@Service
public class MySQLUnitOfWorkFactory implements SQLUnitOfWorkFactory {

	private String jdbcURL;
	private String username;
	private String password;

	/**
	 * Constructs an instance of {@link MySQLUnitOfWorkFactory}.
	 */
	@Inject
	public MySQLUnitOfWorkFactory(
		@Named("JDBC_URL") final String jdbcURL, 
		@Named("JDBC_USERNAME") final String username, 
		@Named("JDBC_PASSWORD") final String password
	) {
		this.jdbcURL = jdbcURL;
		this.username = username;
		this.password = password;
	}

	/**
	 * Creates a new instance of {@link MySQLUnitOfWork}.
	 *
	 * @return The new instance of {@link MySQLUnitOfWork}.
	 */
	public SQLUnitOfWork create() {
		try {
			Connection connection = DriverManager.getConnection(this.jdbcURL, this.username, this.password);
			connection.setAutoCommit(false);
			return new MySQLUnitOfWork(connection);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
