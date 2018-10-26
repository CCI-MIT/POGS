package edu.mit.cci.pogs.messages;

import org.jooq.tools.json.JSONObject;

public class TaskAttributeMessageContent {
    private String attributeName;
    private String attributeStringValue;
    private Double attributeDoubleValue;
    private Long attributeIntegerValue;
    private String extraData;
    private Boolean loggableAttribute;
    private Boolean mustCreateNewAttribute;
    private Boolean broadcastableAttribute;

    public TaskAttributeMessageContent() {
        mustCreateNewAttribute = false;
        broadcastableAttribute = true;
    }

    public TaskAttributeMessageContent(String attributeName, Boolean loggableAttribute) {
        this.attributeName = attributeName;
        this.loggableAttribute = loggableAttribute;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeStringValue() {
        return attributeStringValue;
    }

    public void setAttributeStringValue(String attributeStringValue) {
        this.attributeStringValue = attributeStringValue;
    }

    public Double getAttributeDoubleValue() {
        return attributeDoubleValue;
    }

    public void setAttributeDoubleValue(Double attributeDoubleValue) {
        this.attributeDoubleValue = attributeDoubleValue;
    }

    public Long getAttributeIntegerValue() {
        return attributeIntegerValue;
    }

    public void setAttributeIntegerValue(Long attributeIntegerValue) {
        this.attributeIntegerValue = attributeIntegerValue;
    }

    public Boolean getLoggableAttribute() {
        return loggableAttribute;
    }

    public void setLoggableAttribute(Boolean loggableAttribute) {
        this.loggableAttribute = loggableAttribute;
    }

    public Boolean getMustCreateNewAttribute() {
        return mustCreateNewAttribute;
    }

    public void setMustCreateNewAttribute(Boolean mustCreateNewAttribute) {
        this.mustCreateNewAttribute = mustCreateNewAttribute;
    }

    public Boolean getBroadcastableAttribute() {
        return broadcastableAttribute;
    }

    public void setBroadcastableAttribute(Boolean broadcastableAttribute) {
        this.broadcastableAttribute = broadcastableAttribute;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public JSONObject toJSON(){
        JSONObject jo = new JSONObject();
        jo.put("attributeName",attributeName);
        jo.put("attributeStringValue",attributeStringValue);
        jo.put("attributeDoubleValue",attributeDoubleValue);
        jo.put("attributeIntegerValue",attributeIntegerValue);
        jo.put("loggableAttribute",loggableAttribute);
        return jo;
    }
}
