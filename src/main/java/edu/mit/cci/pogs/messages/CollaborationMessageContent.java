package edu.mit.cci.pogs.messages;

import org.jooq.tools.json.JSONObject;

public class CollaborationMessageContent {

    private String message;
    private CollaborationMessage.CollaborationType collaborationType;

    private String messageType;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CollaborationMessage.CollaborationType getCollaborationType() {
        return collaborationType;
    }

    public void setCollaborationType(CollaborationMessage.CollaborationType collaborationType) {
        this.collaborationType = collaborationType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

}
