package edu.mit.cci.pogs.ot.api;

import edu.mit.cci.pogs.ot.api.components.DeleteComponent;
import edu.mit.cci.pogs.ot.api.components.InsertComponent;
import edu.mit.cci.pogs.ot.api.components.RetainComponent;
import edu.mit.cci.pogs.ot.dto.OperationComponentDto;
import edu.mit.cci.pogs.ot.dto.OperationDto;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class Operation {

    private String padId;
    private Integer id;
    private Integer parentId;

    private int baseLength = 0;
    private int targetLength = 0;
    private List<OperationComponent> components = new ArrayList<>();

    private OperationMetaData metaData;


    private Operation(int parentId) {
        this.parentId = parentId;
    }


    public String getPadId() {
        return padId;
    }

    public void setPadId(String padId) {
        this.padId = padId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
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
        if (!components.isEmpty()) {
            final int lastComponentIndex = components.size() - 1;
            final OperationComponent lastComponent = components.get(lastComponentIndex);
            if (lastComponent.getClass() == component.getClass()) {
                components.set(lastComponentIndex, lastComponent.merge(component));
                return;
            }
        }
        components.add(component);
    }

    public static Operation begin() {
        return new Operation(-1);
    }

    public static Operation begin(Integer parentId) {
        return new Operation(parentId != null ? parentId : -1);
    }

    public static Operation fromDto(OperationDto dto) {
        final Operation operation = new Operation(dto.getParentId());
        operation.setPadId(dto.getPadId());
        operation.setId(dto.getId());
        operation.setMetaData(dto.getMetaData());

        for (OperationComponentDto component : dto.getComponents()) {
            operation.add(component.toComponent());
        }
        return operation;
    }

    public OperationDto toDto() {
        final OperationDto dto = new OperationDto();
        dto.setParentId(parentId);
        dto.setPadId(padId);
        dto.setId(id);
        dto.setMetaData(metaData);

        final ArrayList<OperationComponentDto> componentDtos = new ArrayList<>();
        for (OperationComponent component : components) {
            componentDtos.add(OperationComponentDto.from(component));
            dto.setComponents(componentDtos);
        }
        return dto;
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

    public Operation withPadId(String padId) {
        this.padId = padId;
        return this;
    }

    public Operation withMetaData(OperationMetaData metaData) {
        this.setMetaData(metaData);
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
            throw new IllegalArgumentException(String.format(
                    "Operation cannot be applied to text of length %d: %s", text.length(), this));
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

        if (metaData != null &&
                !Objects.equals(metaData.getAuthorId(), operationB.metaData.getAuthorId())) {
            throw new IllegalArgumentException("Cannot compose operations from different authors");
        }

        Operation composedOperation = Operation.begin()
                .withPadId(padId)
                .withMetaData(metaData);

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

    /**
     * Transforms an operation to be applied after this operation has been applied.
     *
     * The following must hold:
     * a.transform(b) = [a’, b’], where a.compose(b’) == b.compose(a’)
     *
     * @param operationB The operation to be transformed.
     * @return a pair of operations, each to be applied after this or the passed operation
     */
    public TransformedOperationPair transform(Operation operationB) {
        Operation operationA = this;

        if (operationA.baseLength != operationB.baseLength) {
            throw new IllegalArgumentException(String.format(
                    "Both operations must have the same baseLength: operationA.baseLength = %d, "
                            + "operationB.baseLength = %d.\nOperationA: %s\nOperationB: %s",
                    operationA.baseLength, operationB.baseLength, operationA, operationB));
        }

        if (!Objects.equals(operationA.parentId, operationB.parentId)) {
            throw new IllegalArgumentException("Both operations must have the same parent.");
        }

        Operation operationAPrime = Operation.begin(operationA.getId())
                                             .withPadId(padId)
                                             .withMetaData(operationA.getMetaData());
        Operation operationBPrime = Operation.begin(operationA.getId())
                                             .withPadId(padId)
                                             .withMetaData(operationB.getMetaData());

        Deque<OperationComponent> componentsA = new ArrayDeque<>(components);
        Deque<OperationComponent> componentsB = new ArrayDeque<>(operationB.components);

        while (!(componentsA.isEmpty() && componentsB.isEmpty())) {
            OperationComponent componentA = componentsA.peek();
            OperationComponent componentB = componentsB.peek();

            if (isInsert(componentA)) {
                operationAPrime.add(componentsA.pop());
                operationBPrime.retain(componentA.getCharactersAdded());
                continue;
            }
            if (isInsert(componentB)) {
                operationBPrime.add(componentsB.pop());
                operationAPrime.retain(componentB.getCharactersAdded());
                continue;
            }

            if (componentA == null) {
                throw new IllegalArgumentException("Operation a is too short.");
            }

            if (componentB == null) {
                throw new IllegalArgumentException("Operation b is too short.");
            }

            OperationComponent shorterComponent;
            if (componentA.size() == componentB.size()) {
                shorterComponent = componentA;
                componentsA.remove();
                componentsB.remove();
            } else if (componentA.size() > componentB.size()) {
                shorterComponent = componentB;
                advance(componentsA, shorterComponent.size());
                componentsB.remove();
            } else {
                shorterComponent = componentA;
                advance(componentsB, shorterComponent.size());
                componentsA.remove();
            }


            if (isRetain(componentA) && isRetain(componentB)) {
                operationAPrime.add(shorterComponent);
                operationBPrime.add(shorterComponent);
            } else if (isDelete(componentA) && isRetain(componentB)) {
                final String deletePayload = componentA.getPayload();
                final String minLengthPayload = deletePayload.substring(0, shorterComponent.size());
                operationAPrime.delete(minLengthPayload);
            } else if (isRetain(componentA) && isDelete(componentB)) {
                final String deletePayload = componentB.getPayload();
                final String minLengthPayload = deletePayload.substring(0, shorterComponent.size());
                operationBPrime.delete(minLengthPayload);
            }
            //unhandled case: both are delete components --> we don't need to do anything
        }

        return new TransformedOperationPair(operationAPrime, operationBPrime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id).append("parentId", parentId)
                .append("baseLength", baseLength)
                .append("targetLength", targetLength)
                .append("components", components)
                .append("metaData", metaData)
                .toString();
    }

    public static class TransformedOperationPair {

        private Operation aPrime;
        private Operation bPrime;

        private TransformedOperationPair(Operation aPrime, Operation bPrime) {
            this.aPrime = aPrime;
            this.bPrime = bPrime;
        }

        public Operation getAPrime() {
            return aPrime;
        }

        public Operation getBPrime() {
            return bPrime;
        }
    }
}
