package edu.mit.cci.pogs.messages;

public class CollaborationMessageContent {

    private String message;
    private CollaborationMessage.CollaborationType type;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CollaborationMessage.CollaborationType getType() {
        return type;
    }

    public void setType(CollaborationMessage.CollaborationType type) {
        this.type = type;
    }
}
