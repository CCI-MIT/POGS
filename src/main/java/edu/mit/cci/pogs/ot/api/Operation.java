package edu.mit.cci.pogs.ot.api;

import edu.mit.cci.pogs.ot.api.components.DeleteComponent;
import edu.mit.cci.pogs.ot.api.components.InsertComponent;
import edu.mit.cci.pogs.ot.api.components.RetainComponent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Operation {

    private int id;
    private int parentId;

    private int baseLength = 0;
    private int targetLength = 0;
    private List<OperationComponent> components = new ArrayList<>();

    private OperationMetaData metaData;

    private Operation() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public OperationMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(OperationMetaData metaData) {
        this.metaData = metaData;
    }


    public void add(OperationComponent component) {
        baseLength += component.getRetain() + component.getCharactersRemoved();
        targetLength += component.getRetain() + component.getCharactersAdded();
        if (targetLength < 0) {
            throw new IllegalArgumentException("Operation would reduce target length below zero.");
        }
        components.add(component);
    }

    public static Operation begin() {
        return new Operation();
    }

    public Operation retain(int retain) {
        add(new RetainComponent(retain));
        return this;
    }

    public Operation insert(String payload) {
        add(new InsertComponent(payload));
        return this;
    }

    public Operation delete(String payload) {
        add(new DeleteComponent(payload));
        return this;
    }

    /**
     * Applies this operation to the given text.
     *
     * @param text the input text.
     * @return the modified text, after this operation was applied to it.
     */
    public String apply(String text) {
        if (text.length() != baseLength) {
            throw new IllegalArgumentException("Operation cannot be applied to text of length "
                    + text.length());
        }
        String outputText = text;
        int position = 0;
        for (OperationComponent component : components) {
            outputText = component.apply(outputText, position);
            position += component.getRetain() + component.getCharactersAdded();
        }
        return outputText;
    }

    /**
     * Composes this operation with another operation, that is applied after this one.
     *
     * This produces one operation that, whose output is equal to that of this operation and the
     * given operation being applied consecutively.
     *
     * The following must hold:
     * b.apply(a.apply(S)) == a.compose(b).apply(S)
     *
     * @param operationB The operation to compose with this one.
     * @return An operation that applies this operation and then the passed operation.
     */
    public Operation compose(Operation operationB) {

        if (targetLength != operationB.baseLength) {
            throw new IllegalArgumentException(String.format("The second operation's "
                            + "baseLength (%d) must be the first operation's targetLength (%d).",
                    operationB.baseLength, targetLength));
        }

        Operation composedOperation = Operation.begin();

        Deque<OperationComponent> componentsA = new ArrayDeque<>(components);
        Deque<OperationComponent> componentsB = new ArrayDeque<>(operationB.components);

        while (!(componentsA.isEmpty() && componentsB.isEmpty())) {
            OperationComponent componentA = componentsA.peek();
            OperationComponent componentB = componentsB.peek();

            if (isDelete(componentA)) {
                composedOperation.add(componentsA.pop());
                continue;
            }

            if (isInsert(componentB)) {
                composedOperation.add(componentsB.pop());
                continue;
            }

            if (componentA == null) {
                throw new IllegalArgumentException("Operation a is too short.");
            }

            if (componentB == null) {
                throw new IllegalArgumentException("Operation b is too short.");
            }

            if (componentA.size() > componentB.size()) {
                advance(componentsA, componentB.size());
                composedOperation.add(componentsB.pop());
            } else if (componentA.size() < componentB.size()) {
                composedOperation.add(componentsA.pop());
                advance(componentsB, componentA.size());
            } else {
                //components have the same size
                if (isRetain(componentA) && isRetain(componentB)) {
                    composedOperation.add(componentsA.pop());
                    componentsB.remove();
                } else if (isRetain(componentA) && isDelete(componentB)) {
                    composedOperation.add(componentsB.pop());
                    componentsA.remove();
                } else if (isInsert(componentA) && isDelete(componentB)) {
                    componentsA.remove();
                    componentsB.remove();
                } else if (isInsert(componentA) && isRetain(componentB)) {
                    composedOperation.add(componentsA.pop());
                    componentsB.remove();
                }
            }
        }

        return composedOperation;
    }

    private static boolean isRetain(OperationComponent component) {
        return component instanceof RetainComponent;
    }

    private static boolean isInsert(OperationComponent component) {
        return component instanceof InsertComponent;
    }

    private static boolean isDelete(OperationComponent component) {
        return component instanceof DeleteComponent;
    }

    private static void advance(Deque<OperationComponent> components, int advanceBy) {
        final OperationComponent oldComponent = components.pop();
        components.addFirst(oldComponent.advance(advanceBy));
    }
}
