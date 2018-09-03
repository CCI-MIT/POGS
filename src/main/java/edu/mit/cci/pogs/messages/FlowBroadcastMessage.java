package edu.mit.cci.pogs.messages;

import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;

public class FlowBroadcastMessage extends PogsMessage<FlowBroadcastMessageContent> {

    private SessionWrapper sessionWrapper;

    public FlowBroadcastMessage(SessionWrapper sessionWrapper) {
        this.sessionWrapper = sessionWrapper;
        this.setType(MessageType.FLOW_BROADCAST);

        this.setSender("server");
        this.content = new FlowBroadcastMessageContent();

        
        this.content.setNextUrl(sessionWrapper.getNextUrl());
        this.content.setSecondsRemainingCurrentUrl(sessionWrapper.getSecondsRemainingForCurrentUrl().toString());
        this.content.setSecondsRemainingForSession(sessionWrapper.getSecondsRemainingForSession().toString());
        this.content.setSecondsRemainingForCurrentRound(sessionWrapper.getSecondsRemainingForCurrentRound().toString());

        //System.out.println("nextUrl:" + this.content.getNextUrl());
        //System.out.println("secondsRemainingCurrentUrl:" + this.content.getSecondsRemainingCurrentUrl());
        //System.out.println("secondsRemainingForSession:" + this.content.getSecondsRemainingForSession());
        //System.out.println("secondsRemainingForCurrentRound:" + this.content.getSecondsRemainingForCurrentRound());

    }

    public String getSpecificPublicTopic() {
        return "/topic/public/flow/" + sessionWrapper.getId();
    }


}

class FlowBroadcastMessageContent {

    private String nextUrl;
    private String secondsRemainingCurrentUrl;
    private String secondsRemainingForSession;
    private String secondsRemainingForCurrentRound;

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

    public String getSecondsRemainingCurrentUrl() {
        return secondsRemainingCurrentUrl;
    }

    public void setSecondsRemainingCurrentUrl(String secondsRemainingCurrentUrl) {
        this.secondsRemainingCurrentUrl = secondsRemainingCurrentUrl;
    }

    public String getSecondsRemainingForSession() {
        return secondsRemainingForSession;
    }

    public void setSecondsRemainingForSession(String secondsRemainingForSession) {
        this.secondsRemainingForSession = secondsRemainingForSession;
    }

    public String getSecondsRemainingForCurrentRound() {
        return secondsRemainingForCurrentRound;
    }

    public void setSecondsRemainingForCurrentRound(String secondsRemainingForCurrentRound) {
        this.secondsRemainingForCurrentRound = secondsRemainingForCurrentRound;
    }
}
