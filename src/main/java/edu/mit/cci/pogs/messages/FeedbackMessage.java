package edu.mit.cci.pogs.messages;

import java.util.Map;

import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;

public class FeedbackMessage extends PogsMessage<FeedbackMessageContent> {



    public FeedbackMessage(SessionWrapper sw, Long completedTaskId, Map<String, Integer> subjectsParticipations){
        this.setSessionId(sw.getId().toString());

        this.completedTaskId = completedTaskId + "";
        this.content = new FeedbackMessageContent(subjectsParticipations, completedTaskId);
        this.setSender(null);
        this.setReceiver(null);
        this.setType(MessageType.COLLABORATION_MESSAGE);

    }

    public String getSpecificPublicTopic() {
        return "/topic/public/task/"+ this.completedTaskId
                + "/collaboration";
    }

}
