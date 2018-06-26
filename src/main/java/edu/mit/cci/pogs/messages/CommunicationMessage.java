package edu.mit.cci.pogs.messages;

public class CommunicationMessage extends PogsMessage<CommunicationMessageContent> {
    public enum CommunicationType {

        JOINED, //To add to panel if not there
        MESSAGE,//NormalMessage
        IS_TYPING,//typing event


        REQUEST_CHAT,//Dyadic
        RESPOND_CHAT //Dyadic
    }
}
class CommunicationMessageContent {
    private String message;
    private CommunicationMessage.CommunicationType type;
    private String channel;

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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}

