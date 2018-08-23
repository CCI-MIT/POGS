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
}
