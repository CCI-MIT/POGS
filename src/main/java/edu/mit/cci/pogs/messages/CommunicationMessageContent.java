package edu.mit.cci.pogs.messages;

import org.jooq.tools.json.JSONObject;

public class CommunicationMessageContent {
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

    public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        jo.put("message", message);
        jo.put("type", type.name().toString());
        jo.put("channel", channel);

        return jo;

    }
}
