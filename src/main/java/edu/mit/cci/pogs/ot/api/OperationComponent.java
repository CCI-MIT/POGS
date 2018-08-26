package edu.mit.cci.pogs.ot.api;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

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
        if (lengthChange < 0) {
            return -lengthChange;
        }
        return 0;
    }

    public int getLengthChange() {
        return lengthChange;
    }

    public abstract OperationComponent advance(int advanceBy);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OperationComponent)) {
            return false;
        }
        OperationComponent component = (OperationComponent) o;
        //equivalence depends on the class as well
        return getClass() == component.getClass()
                && getRetain() == component.getRetain()
                && getLengthChange() == component.getLengthChange()
                && Objects.equals(getPayload(), component.getPayload());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), getRetain(), getPayload(), getLengthChange());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("retain", retain)
                .append("payload", payload)
                .append("lengthChange", lengthChange)
                .toString();
    }
}
