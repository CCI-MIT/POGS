package edu.mit.cci.pogs.ot.api.components;

import edu.mit.cci.pogs.ot.api.OperationComponent;

public class InsertComponent extends OperationComponent {

    public InsertComponent(String payload) {
        super(0, payload, payload.length());
    }

    @Override
    public String apply(String text, int cursorPosition) {
        String textBefore = text.substring(0, cursorPosition);
        String textAfter = text.substring(cursorPosition);
        return textBefore + getPayload() + textAfter;
    }

    @Override
    public OperationComponent merge(OperationComponent otherComponent) {
        if (!(otherComponent instanceof InsertComponent)) {
            throw new IllegalArgumentException("Cannot merge with component of type: "
                    + otherComponent.getClass());
        }
        return new InsertComponent(getPayload() + otherComponent.getPayload());
    }

    @Override
    public OperationComponent advance(int advanceBy) {
        return new InsertComponent(getPayload().substring(advanceBy));
    }
}
