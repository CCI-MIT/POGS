package edu.mit.cci.pogs.view.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.util.Date;

import edu.mit.cci.pogs.messages.CheckInMessage;
import edu.mit.cci.pogs.messages.CommunicationMessage;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;

@Controller
public class WorkspaceCheckinWSController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private SubjectDao subjectDao;


    @MessageMapping("/checkIn.sendMessage")
    public void getCheckin(@Payload CheckInMessage pogsMessage, SimpMessageHeaderAccessor headerAccessor) {
        Long completedTaskId = null;
        if(pogsMessage.getCompletedTaskId()!=null) {
            completedTaskId = Long.parseLong(pogsMessage.getCompletedTaskId());
        }
        Long sessionId = Long.parseLong(pogsMessage.getSessionId());

        if (pogsMessage.getContent().getType().equals(CommunicationMessage.CommunicationType.CHECK_IN.name())) {
            headerAccessor.getSessionAttributes().put("externalUserId", pogsMessage.getSender());
        }


        //save in the logs what kinds of events?

        messagingTemplate.convertAndSend("/topic/public/checkin/" + sessionId, pogsMessage);
    }
}
