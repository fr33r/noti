package infrastructure.query;

import infrastructure.UnitOfWork;
import org.jvnet.hk2.annotations.Contract;

@Contract
public abstract class QueryFactory<T> {

  public abstract Query<T> createQuery(UnitOfWork unitOfWork);
}
