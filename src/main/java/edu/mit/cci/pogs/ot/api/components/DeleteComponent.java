package edu.mit.cci.pogs.ot.api.components;

import edu.mit.cci.pogs.ot.api.OperationComponent;

public class DeleteComponent extends OperationComponent {

    public DeleteComponent(String payload) {
        super(0, payload, -payload.length());
    }

    @Override
    public String apply(String text, int cursorPosition) {
        String textBefore = text.substring(0, cursorPosition);
        String textAfter = text.substring(cursorPosition);
        if (!textAfter.startsWith(getPayload())) {
            throw new IllegalStateException(String.format("This operation cannot be applied "
                            + "to text '%s' at position %d. Expected '%s', found '%s'.",
                    text, cursorPosition, getPayload(), textAfter.substring(0, getPayload().length())
            ));
        }
        return textBefore + textAfter.substring(getPayload().length());
    }

    @Override
    public OperationComponent merge(OperationComponent otherComponent) {
        if (!(otherComponent instanceof DeleteComponent)) {
            throw new IllegalArgumentException("Cannot merge with component of type: "
                    + otherComponent.getClass());
        }
        return new DeleteComponent(getPayload() + otherComponent.getPayload());
    }

    @Override
    public OperationComponent advance(int advanceBy) {
        return new DeleteComponent(getPayload().substring(advanceBy));
    }
}
