package infrastructure;

import org.jvnet.hk2.annotations.Contract;

@Contract
public abstract class QueryFactory<T> {

  public abstract Query<T> createQuery(SQLUnitOfWork unitOfWork);
}
