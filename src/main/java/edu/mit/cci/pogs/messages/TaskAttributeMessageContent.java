package edu.mit.cci.pogs.messages;

import org.jooq.tools.json.JSONObject;

public class TaskAttributeMessageContent {
    private String attributeName;
    private String attributeStringValue;
    private Double attributeDoubleValue;
    private Long attributeIntegerValue;
    private Boolean loggableAttribute;

    public TaskAttributeMessageContent() {

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
