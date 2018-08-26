package edu.mit.cci.pogs.ot.api.components;

import edu.mit.cci.pogs.ot.api.OperationComponent;

public class RetainComponent extends OperationComponent {

    public RetainComponent(int retain) {
        super(retain, "", 0);
    }

    @Override
    public String apply(String text, int cursorPosition) {
        return text;
    }

    @Override
    public OperationComponent merge(OperationComponent otherComponent) {
        if (!(otherComponent instanceof RetainComponent)) {
            throw new IllegalArgumentException("Cannot merge with component of type: "
                    + otherComponent.getClass());
        }
        return new RetainComponent(getRetain() + otherComponent.getRetain());
    }

    @Override
    public OperationComponent advance(int advanceBy) {
        return new RetainComponent(getRetain() - advanceBy);
    }
}
