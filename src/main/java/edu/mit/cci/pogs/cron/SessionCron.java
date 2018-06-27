package edu.mit.cci.pogs.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.mit.cci.pogs.messages.FlowBroadcastMessage;
import edu.mit.cci.pogs.messages.PogsMessage;
import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.wrappers.TeamWrapper;
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

    @Scheduled(fixedRate = RATE, initialDelay = DELAY)
    public void checkForSessionInitialization() {
        //_log.debug("Checking for sessions to be configured...");
        sessionService.initializeSessionRunners();

    }

    @Scheduled(fixedRate = RATE, initialDelay = RATE)
    public void sendSessionFlowBroadCastMessages(){
        for(SessionRunner runner : SessionRunner.getLiveRunners()){
            FlowBroadcastMessage pogsMessage = new FlowBroadcastMessage(runner.getSession());
            messagingTemplate.convertAndSend(pogsMessage.getSpecificPublicTopic(), pogsMessage);


        }
    }
}
