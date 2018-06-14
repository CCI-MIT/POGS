package edu.mit.cci.pogs.messages;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TeamWrapper;

public class FlowBroadcastMessage extends PogsMessage {

    private SessionWrapper sessionWrapper;

    public FlowBroadcastMessage(SessionWrapper sessionWrapper) {
        this.sessionWrapper = sessionWrapper;
        this.setType(MessageType.FLOW_BROADCAST);

        this.setSender("server");
        JSONObject jo = new JSONObject();
        try {
            jo.put("nextUrl", sessionWrapper.getNextUrl());
            jo.put("secondsRemainingCurrentUrl", sessionWrapper.getSecondsRemainingForCurrentUrl().toString());
            jo.put("secondsRemainingForSession", sessionWrapper.getSecondsRemainingForSession().toString());
            jo.put("secondsRemainingForCurrentRound", sessionWrapper.getSecondsRemainingForCurrentRound().toString());
            this.setContent(jo.toString());
        }catch (JSONException je){

        }
    }

    public String getSpecificPublicTopic(){
        return "/topic/public/flow/" + sessionWrapper.getId();
    }


}
