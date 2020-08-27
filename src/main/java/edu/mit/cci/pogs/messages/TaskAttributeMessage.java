package edu.mit.cci.pogs.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.jooq.tools.json.JSONObject;


public class TaskAttributeMessage extends PogsMessage<TaskAttributeMessageContent> {

    public TaskAttributeMessage() {

    }

    public TaskAttributeMessage(MessageType type, TaskAttributeMessageContent content,
            String sender, String receiver, String completedTaskId, String sessionId) {
        super(type, content, sender, receiver, completedTaskId, sessionId);
    }

    @JsonIgnore
    public Boolean getLoggableAttribute() {
        return content.getLoggableAttribute();
    }

    @JsonIgnore
    public Boolean getBroadcastableAttribute() {
        return content.getBroadcastableAttribute();
    }

    @JsonIgnore
    public Boolean getShouldUpdateExistingAttribute() {
        return content.getShouldUpdateExistingAttribute();
    }

    @JsonIgnore
    public Boolean getMustCreateNewAttribute() {
        return content.getMustCreateNewAttribute();
    }

    @JsonIgnore
    public String getAttributeName() {
        return content.getAttributeName();
    }

    @JsonIgnore
    public String getExtraData() {
        return content.getExtraData();
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

    @JsonIgnore
    public String getSummaryDescription(){
        return content.getSummaryDescription();
    }




    public JSONObject toJSON() {return content.toJSON();}

}

