package api.health;

import com.codahale.metrics.health.HealthCheck;
import infrastructure.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;

public final class DatabaseHealthCheck extends HealthCheck {

  private final ConnectionFactory connectionFactory;

  public DatabaseHealthCheck(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Override
  public Result check() throws Exception {
    Connection connection = null;
    try {
      connection = this.connectionFactory.createConnection();
      PreparedStatement healthStatement = connection.prepareStatement("SELECT 1;");
      healthStatement.executeQuery();
      connection.commit();
      return Result.healthy();
    } catch (Exception x) {
      if (connection != null) {
        connection.rollback();
        connection.close();
      }
      return Result.unhealthy(x.getMessage());
    }
  }
}
