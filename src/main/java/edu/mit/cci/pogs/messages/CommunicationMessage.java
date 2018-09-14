package edu.mit.cci.pogs.messages;

import org.jooq.tools.json.JSONObject;

public class CommunicationMessage extends PogsMessage<CommunicationMessageContent> {

    public JSONObject toJSON() {
        return this.content.toJSON();
    }
    public enum CommunicationType {

        CHECK_IN,
        JOINED, //To add to panel if not there
        MESSAGE,//NormalMessage
        IS_TYPING,//typing event


        //Dyadic event
        REQUEST_CHAT,
        HANG_UP_CHAT,
        ACCEPT_CHAT,

        STATUS
    }
}


