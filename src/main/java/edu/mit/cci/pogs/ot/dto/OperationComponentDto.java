package edu.mit.cci.pogs.ot.dto;

import edu.mit.cci.pogs.ot.api.OperationComponent;
import edu.mit.cci.pogs.ot.api.components.DeleteComponent;
import edu.mit.cci.pogs.ot.api.components.InsertComponent;
import edu.mit.cci.pogs.ot.api.components.RetainComponent;

public class OperationComponentDto {

    private ComponentType type;
    private int retain;
    private String payload;
    private int lengthChange;

    public static OperationComponentDto from(OperationComponent component) {
        if (component instanceof RetainComponent) {
            final OperationComponentDto dto = new OperationComponentDto();
            dto.type = ComponentType.RETAIN;
            dto.retain = component.getRetain();
            return dto;
        } else if (component instanceof InsertComponent) {
            final OperationComponentDto dto = new OperationComponentDto();
            dto.type = ComponentType.INSERT;
            dto.payload = component.getPayload();
            dto.lengthChange = component.getLengthChange();
            return dto;
        } else if (component instanceof DeleteComponent) {
            final OperationComponentDto dto = new OperationComponentDto();
            dto.type = ComponentType.DELETE;
            dto.payload = component.getPayload();
            dto.lengthChange = component.getLengthChange();
            return dto;
        }
        throw new IllegalArgumentException("Unknown component type " + component.getClass());
    }

    public OperationComponent toComponent() {
        switch (type) {
            case RETAIN:
                return new RetainComponent(retain);
            case INSERT:
                return new InsertComponent(payload);
            case DELETE:
                return new DeleteComponent(payload);
            default:
                throw new IllegalArgumentException("Unknown component type " + type);
        }
    }

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

    public int getRetain() {
        return retain;
    }

    public void setRetain(int retain) {
        this.retain = retain;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public int getLengthChange() {
        return lengthChange;
    }

    public void setLengthChange(int lengthChange) {
        this.lengthChange = lengthChange;
    }

    public enum ComponentType {
        INSERT, DELETE, RETAIN
    }
}
