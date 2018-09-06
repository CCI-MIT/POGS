package edu.mit.cci.pogs.messages;

import org.jooq.tools.json.JSONObject;

public class CheckInMessage extends PogsMessage<CommunicationMessageContent> {

    public JSONObject toJSON() {
        return this.content.toJSON();
    }
}
