package edu.mit.cci.pogs.ot;

import edu.mit.cci.pogs.ot.api.Operation;
import edu.mit.cci.pogs.ot.api.OperationState;
import edu.mit.cci.pogs.ot.api.impl.OperationStateImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperationService {

    private Map<Long, OperationState> serverStates = new HashMap<>();

    public void initializeState(long padId, String initialText) {
        serverStates.put(padId, new OperationStateImpl(initialText));
    }

    public OperationState getState(long padId) {
        return serverStates.get(padId);
    }

    public void removeState(long padId) {
        serverStates.remove(padId);
    }

    public Operation processOperation(long padId, Operation operation) {
        if (operation.getPadId() != null && operation.getPadId() != padId) {
            throw new IllegalArgumentException(String.format(
                    "This operation's padId %d does not match the given padId %d.",
                    operation.getPadId(), padId));
        }

        final OperationState operationState = serverStates.get(padId);
        if (operationState == null) {
            throw new IllegalArgumentException("There is no server state with the given padId");
        }

        return operationState.apply(operation);
    }

    public List<Operation> getAllOperations(long padId) {
        final OperationState operationState = serverStates.get(padId);
        return operationState.getAllOperations();
    }
}
