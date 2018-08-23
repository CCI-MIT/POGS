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
    public OperationComponent advance(int advanceBy) {
        return new RetainComponent(getRetain() - advanceBy);
    }
}
