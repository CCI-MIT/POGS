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

        CHECK_IN,
        FLOW_BROADCAST,

        TASK_ATTRIBUTE,

        COLLABORATION_MESSAGE,
        OPERATION
    }

    public PogsMessage() {
    }

    public PogsMessage(MessageType type, ContentType content, String sender, String receiver, String completedTaskId,
            String sessionId) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.completedTaskId = completedTaskId;
        this.sessionId = sessionId;
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
