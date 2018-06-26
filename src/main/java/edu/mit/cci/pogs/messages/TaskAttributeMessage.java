package edu.mit.cci.pogs.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class TaskAttributeMessage extends PogsMessage<TaskAttributeMessageContent> {


    @JsonIgnore
    public Boolean getLoggableAttribute() {
        return content.getLoggableAttribute();
    }

    @JsonIgnore
    public String getAttributeName() {
        return content.getAttributeName();
    }

    @JsonIgnore
    public String getAttributeStringValue() {
        return content.getAttributeStringValue();
    }

    @JsonIgnore
    public Double getAttributeDoubleValue() {
        return content.getAttributeDoubleValue();
    }

    @JsonIgnore
    public Long getAttributeIntegerValue() {
        return content.getAttributeIntegerValue();
    }



}

class TaskAttributeMessageContent {
    private String attributeName;
    private String attributeStringValue;
    private Double attributeDoubleValue;
    private Long attributeIntegerValue;
    private Boolean loggableAttribute;

    public TaskAttributeMessageContent() {

    }

    public TaskAttributeMessageContent(String attributeName, String attributeStringValue,
                                       Double attributeDoubleValue, Long attributeIntegerValue) {
        this.attributeName = attributeName;
        this.attributeStringValue = attributeStringValue;
        this.attributeDoubleValue = attributeDoubleValue;
        this.attributeIntegerValue = attributeIntegerValue;
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

}