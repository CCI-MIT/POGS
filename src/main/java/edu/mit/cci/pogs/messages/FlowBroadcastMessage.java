package edu.mit.cci.pogs.messages;

import edu.mit.cci.pogs.model.dao.session.SessionScheduleType;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;

public class FlowBroadcastMessage extends PogsMessage<FlowBroadcastMessageContent> {

    private SessionWrapper sessionWrapper;

    public FlowBroadcastMessage(SessionRunner sessionRunner) {

        this.sessionWrapper = sessionRunner.getSession();
        this.setType(MessageType.FLOW_BROADCAST);

        this.setSender("server");
        this.content = new FlowBroadcastMessageContent();


        this.content.setNextUrl(sessionWrapper.getNextUrl());
        this.content.setCurrentUrl(sessionWrapper.getCurrentUrl());
        this.content.setSecondsRemainingCurrentUrl(sessionWrapper.getSecondsRemainingForCurrentUrl().toString());
        this.content.setSecondsRemainingForSession(sessionWrapper.getSecondsRemainingForSession().toString());
        this.content.setSecondsRemainingForCurrentRound(sessionWrapper.getSecondsRemainingForCurrentRound().toString());
        if(sessionWrapper.isSessionPerpetual()) {
            this.content.setPerpetualSubjectsChosen(sessionRunner.getPerpetualSubjectsMigratedToSpawnedSessions().toString());
        }



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
    private String currentUrl;
    private String secondsRemainingCurrentUrl;
    private String secondsRemainingForSession;
    private String secondsRemainingForCurrentRound;

    private String perpetualSubjectsChosen;

    public String getPerpetualSubjectsChosen() {
        return perpetualSubjectsChosen;
    }

    public void setPerpetualSubjectsChosen(String perpetualSubjectsChosen) {
        this.perpetualSubjectsChosen = perpetualSubjectsChosen;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
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
