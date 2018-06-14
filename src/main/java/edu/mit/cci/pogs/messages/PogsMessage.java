package edu.mit.cci.pogs.messages;

public class PogsMessage {

    private MessageType type;
    private String content;
    private String sender;
    private String receiver;
    private String completedTaskId;
    private String sessionId;


    public enum MessageType {
        COMMUNICATION_MESSAGE,
        COMMUNICATION_JOIN,
        COMMUNICATION_LEAVE,

        FLOW_CHECK_IN,
        FLOW_BROADCAST,

        FLOW_ACK,

        TASK_ATTRIBUTE,

        COLLABORATION_MESSAGE
    }


    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
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
}
