package edu.mit.cci.pogs.ot.dto;

import edu.mit.cci.pogs.ot.api.OperationMetaData;

import java.util.ArrayList;
import java.util.List;

public class OperationDto {

    private String padId;
    private Integer id;
    private int parentId;

    private List<OperationComponentDto> components = new ArrayList<>();

    private OperationMetaData metaData;

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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<OperationComponentDto> getComponents() {
        return components;
    }

    public void setComponents(List<OperationComponentDto> components) {
        this.components = components;
    }

    public OperationMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(OperationMetaData metaData) {
        this.metaData = metaData;
    }
}
