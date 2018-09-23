package api.health;

import com.codahale.metrics.health.HealthCheck;
import infrastructure.SQLUnitOfWork;
import infrastructure.SQLUnitOfWorkFactory;
import java.sql.PreparedStatement;

public final class DatabaseHealthCheck extends HealthCheck {

  private final SQLUnitOfWorkFactory unitOfWorkFactory;

  public DatabaseHealthCheck(SQLUnitOfWorkFactory unitOfWorkFactory) {
    this.unitOfWorkFactory = unitOfWorkFactory;
  }

  @Override
  public Result check() throws Exception {
    SQLUnitOfWork unitOfWork = null;
    try {
      unitOfWork = this.unitOfWorkFactory.create();
      PreparedStatement healthStatement = unitOfWork.createPreparedStatement("SELECT 1;");
      healthStatement.executeQuery();
      unitOfWork.save();
      return Result.healthy();
    } catch (Exception x) {
      unitOfWork.undo();
      return Result.unhealthy(x.getMessage());
    }
  }
}
