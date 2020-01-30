package edu.mit.cci.pogs.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import edu.mit.cci.pogs.messages.CommunicationMessage;
import edu.mit.cci.pogs.messages.CommunicationMessageContent;
import edu.mit.cci.pogs.messages.PogsMessage;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        //logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String externalUserId = (String) headerAccessor.getSessionAttributes().get("externalUserId");
        String latestCompletedTaskId = (String) headerAccessor.getSessionAttributes()
                .get("latestCompletedTaskId");

        if(externalUserId != null) {
            logger.info("User Disconnected : " + externalUserId);

            CommunicationMessage cm = new CommunicationMessage();
            cm.setSender(externalUserId);
            cm.setType(PogsMessage.MessageType.COMMUNICATION_MESSAGE);
            CommunicationMessageContent cmc = new CommunicationMessageContent();
            cmc.setType(CommunicationMessage.CommunicationType.STATUS);
            cmc.setMessage("UNAVAILABLE");

            cm.setContent(cmc);
            messagingTemplate.convertAndSend("/topic/public/task/"+latestCompletedTaskId + "/communication", cm);
        }
    }

}
