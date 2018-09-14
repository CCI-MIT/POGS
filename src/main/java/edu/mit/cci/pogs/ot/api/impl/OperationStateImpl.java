package edu.mit.cci.pogs.ot.api.impl;

import edu.mit.cci.pogs.ot.api.Operation;
import edu.mit.cci.pogs.ot.api.Operation.TransformedOperationPair;
import edu.mit.cci.pogs.ot.api.OperationState;

import java.util.ArrayList;
import java.util.List;

public class OperationStateImpl implements OperationState {

    private List<Operation> operations = new ArrayList<>();
    private String text;

    public OperationStateImpl() {
        this("");
    }

    public OperationStateImpl(String initialText) {
        text = initialText;
    }

    @Override
    public synchronized Operation apply(Operation operation) {
        if (operation.getParentId() < operations.size() - 1) {
            operation = transform(operation);
        }
        operation.setId(operations.size());
        text = operation.apply(text);
        operations.add(operation);
        return operation;
    }

    @Override
    public Operation transform(Operation operation) {
        if (operation.getParentId() >= operations.size()) {
            throw new IllegalArgumentException(String.format("Operation with parent %d is "
                            + "not based on any operations in this state (size = %d)",
                    operation.getParentId(), operations.size()));
        }

        final List<Operation> concurrentOperations = findOperationsSince(operation.getParentId());

        Operation transformedOperation = operation;
        for (Operation concurrentOperation : concurrentOperations) {
            final TransformedOperationPair transformedOperationPair =
                    concurrentOperation.transform(transformedOperation);
            transformedOperation = transformedOperationPair.getBPrime();
        }
        return transformedOperation;
    }

    @Override
    public List<Operation> findOperationsSince(int operationId) {
        return new ArrayList<>(operations.subList(operationId + 1, operations.size()));
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public List<Operation> getAllOperations() {
        return new ArrayList<>(operations);
    }
}
