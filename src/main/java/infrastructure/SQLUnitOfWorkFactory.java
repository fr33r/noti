package infrastructure;

import infrastructure.SQLUnitOfWork;

import org.jvnet.hk2.annotations.Contract;

/**
 * Represents the contract that is to be implemented by any class that
 * wishes to serve as a factory for instances implementing {@link SQLUnitOfWork}.
 */
@Contract
public interface SQLUnitOfWorkFactory {

    /**
     * Creates an instance of a class that implements the {@link SQLUnitOfWork} interface.
     *
     * @return The instance of a class that implements the {@link SQLUnitOfWork} interface.
     */
    SQLUnitOfWork create();
}
