package edu.mit.cci.pogs.ot;

import edu.mit.cci.pogs.ot.api.Operation;
import edu.mit.cci.pogs.ot.api.OperationState;
import edu.mit.cci.pogs.ot.api.impl.OperationStateImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class OperationService {

    private Map<String, OperationState> serverStates = new HashMap<>();

    public OperationState initializeState(String padId, String initialText) {
        final OperationStateImpl state = new OperationStateImpl(initialText);
        serverStates.put(padId, state);
        return state;
    }

    public OperationState getState(String padId) {
        return serverStates.get(padId);
    }

    public void removeState(String padId) {
        serverStates.remove(padId);
    }

    public Operation processOperation(String padId, Operation operation) {
        return processOperation(padId, operation, false);
    }

    public synchronized  Operation processOperation(String padId, Operation operation, boolean create) {
        if (operation.getPadId() != null && !Objects.equals(operation.getPadId(), padId)) {
            throw new IllegalArgumentException(String.format(
                    "This operation's padId %s does not match the given padId %s.",
                    operation.getPadId(), padId));
        }

        OperationState operationState = serverStates.get(padId);
        if (operationState == null) {
            if (create) {
                operationState = initializeState(padId, "");
            } else {
                throw new IllegalArgumentException("There is no server state with the given padId");
            }
        }
        System.out.println("Before apply: " + operation.getId());
        return operationState.apply(operation);
    }

    public List<Operation> getAllOperations(String padId) {
        final OperationState operationState = serverStates.get(padId);
        return operationState.getAllOperations();
    }
}
