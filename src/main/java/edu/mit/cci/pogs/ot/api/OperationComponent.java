package edu.mit.cci.pogs.ot.api;

public abstract class OperationComponent {

    private int retain;
    private String payload;
    private int lengthChange;

    protected OperationComponent(int retain, String payload, int lengthChange) {
        this.retain = retain;
        this.payload = payload;
        this.lengthChange = lengthChange;
    }

    public abstract String apply(String text, int cursorPosition);

    public abstract OperationComponent merge(OperationComponent otherComponent);

    public int getRetain() {
        return retain;
    }

    public String getPayload() {
        return payload;
    }

    /**
     * A component's size is the amount of characters it spans.
     *
     * This is either its retain value or its payload length.
     *
     * @return the amount of characters this component spans.
     */
    public int size() {
        return retain + payload.length();
    }

    public int getCharactersAdded() {
        if (lengthChange > 0) {
            return lengthChange;
        }
        return 0;
    }

    public int getCharactersRemoved() {
        if(lengthChange < 0) {
            return -lengthChange;
        }
        return 0;
    }

    public int getLengthChange() {
        return lengthChange;
    }

    public abstract OperationComponent advance(int advanceBy);
}
