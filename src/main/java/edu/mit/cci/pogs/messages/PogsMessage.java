package edu.mit.cci.pogs.messages;

public class PogsMessage <ContentType>{

    protected MessageType type;
    protected ContentType content;
    protected String sender;
    protected String receiver;
    protected String completedTaskId;
    protected String sessionId;


    public enum MessageType {
        COMMUNICATION_MESSAGE,

        FLOW_CHECK_IN,
        FLOW_BROADCAST,

        TASK_ATTRIBUTE,

        COLLABORATION_MESSAGE
    }

    public PogsMessage() {
    }


    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public ContentType getContent() {
        return content;
    }

    public void setContent(ContentType content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getCompletedTaskId() {
        return completedTaskId;
    }


    public void setCompletedTaskId(String completedTaskId) {
        this.completedTaskId = completedTaskId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
