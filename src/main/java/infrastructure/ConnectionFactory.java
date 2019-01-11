package infrastructure;

import java.sql.Connection;

public abstract class ConnectionFactory {

  public abstract Connection createConnection();
}
