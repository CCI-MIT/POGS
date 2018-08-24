package edu.mit.cci.pogs.ot.api;

import java.util.List;

public interface OperationState {

    /**
     * Applies the operation to this state.
     *
     * The operation must be based on an operation in this state and will be transformed if necessary.
     *
     * @param operation the operation to be applied to the state
     * @throws IllegalArgumentException if {@link Operation#getParentId()} returns an unknown id
     */
    Operation apply(Operation operation);

    /**
     * Transforms an operation so that it is based on the most recent operation in thsi state.
     *
     * The operation must be based on a known operation in this state.
     *
     * @param operation the operation to be transformed
     * @return an operation that is based on the most recent operation in this state
     * @throws IllegalArgumentException if {@link Operation#getParentId()} returns an unknown id
     */
    Operation transform(Operation operation);

    /**
     * Finds all operations that occurred after the given operation.
     *
     * @param operationId an operation id known to this state
     * @return all operations that occurred since the operation was added
     */
    List<Operation> findOperationsSince(int operationId);

    /**
     * Returns the text resulting from all operations known to this state being applied in order.
     *
     * @return the current text
     */
    String getText();
}
