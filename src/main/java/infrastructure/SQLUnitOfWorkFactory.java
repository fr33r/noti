package infrastructure;

import org.jvnet.hk2.annotations.Contract;

/**
 * Represents the contract that is to be implemented by any class that wishes to serve as a factory
 * for instances implementing {@link infrastructure.SQLUnitOfWork}.
 */
@Contract
public interface SQLUnitOfWorkFactory {

  /**
   * Creates an instance of a class that implements the {@link infrastructure.SQLUnitOfWork}
   * interface.
   *
   * @return The instance of a class that implements the {@link infrastructure.SQLUnitOfWork}
   *     interface.
   */
  SQLUnitOfWork create();
}
