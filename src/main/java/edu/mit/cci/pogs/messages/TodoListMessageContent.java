package edu.mit.cci.pogs.messages;

public class TodoListMessageContent {

    private String message;
    private CommunicationMessage.CommunicationType type;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CommunicationMessage.CommunicationType getType() {
        return type;
    }

    public void setType(CommunicationMessage.CommunicationType type) {
        this.type = type;
    }
}
