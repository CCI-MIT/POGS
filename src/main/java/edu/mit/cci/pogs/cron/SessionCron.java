package edu.mit.cci.pogs.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

import edu.mit.cci.pogs.messages.FeedbackMessage;
import edu.mit.cci.pogs.messages.FlowBroadcastMessage;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.SessionRunnerManager;
import edu.mit.cci.pogs.runner.wrappers.RoundWrapper;
import edu.mit.cci.pogs.service.SessionService;

@Component
public class SessionCron {

    private static final long RATE = 10_000;
    private static final long DELAY = 10_000;

    private static final Logger _log = LoggerFactory.getLogger(SessionCron.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;


    public SessionCron() {
        _log.info("Initializing session cron with rate = {}s", RATE / 1000);
    }

    @Scheduled(fixedRate = RATE)
    public void checkForSessionInitialization() {
        //_log.debug("Checking for sessions to be configured...");
        sessionService.initializeSessionRunners();
        sessionService.initializePerpetualSessionRunners();

    }

    @Scheduled(fixedRate = RATE, initialDelay = RATE)
    public void sendSessionFlowBroadCastMessages(){
        for(SessionRunner runner : SessionRunnerManager.getLiveRunners()){
            FlowBroadcastMessage pogsMessage = new FlowBroadcastMessage(runner.getSession());

            messagingTemplate.convertAndSend(pogsMessage.getSpecificPublicTopic(), pogsMessage);

        }
    }

    @Scheduled(fixedRate = 1000, initialDelay = 1000)
    public void sendFeedbackStatistics(){
        for(SessionRunner runner : SessionRunnerManager.getLiveRunners()){
            RoundWrapper rw = runner.getSession().getCurrentRound();
            if(rw!=null){ //only start sending the feedback after the round is setup
                Map<Long, Map<String, Integer>> allFeedbacksForSession = runner.getSession().getFeedbackCounter();
                Iterator<Long> allCompletedTasks = allFeedbacksForSession.keySet().iterator();
                while(allCompletedTasks.hasNext()) {
                    Long completedTaskId = allCompletedTasks.next();
                    Map<String, Integer> subjectsParticipations = allFeedbacksForSession.get(completedTaskId);
                    FeedbackMessage pogsMessage = new FeedbackMessage(runner.getSession(),completedTaskId, subjectsParticipations);
                    messagingTemplate.convertAndSend(pogsMessage.getSpecificPublicTopic(), pogsMessage);
                }

            }

        }
    }
}
