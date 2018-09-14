package edu.mit.cci.pogs.view.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.util.Date;

import edu.mit.cci.pogs.messages.CommunicationMessage;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;

@Controller
public class WorkspaceCommunicationWSController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private SubjectDao subjectDao;

    @MessageMapping("/communication.sendMessage")
    public void getCheckin(@Payload CommunicationMessage pogsMessage, SimpMessageHeaderAccessor headerAccessor) {

        Long completedTaskId = Long.parseLong(pogsMessage.getCompletedTaskId());
        Long sessionId = Long.parseLong(pogsMessage.getSessionId());

        if (pogsMessage.getContent().getType().equals(CommunicationMessage.CommunicationType.JOINED.name())) {
            headerAccessor.getSessionAttributes().put("externalUserId", pogsMessage.getSender());
            headerAccessor.getSessionAttributes().put("latestCompletedTaskId", completedTaskId);
        } else {
            Subject sender = subjectDao.getByExternalId(pogsMessage.getSender());
            if(sender!=null) {
                EventLog el = new EventLog();
                el.setCompletedTaskId(completedTaskId);
                el.setSessionId(sessionId);
                el.setSender(pogsMessage.getSender());
                el.setReceiver(pogsMessage.getReceiver());
                el.setTimestamp(new Timestamp(new Date().getTime()));
                el.setEventType(pogsMessage.getType().name().toString());
                el.setEventContent(pogsMessage.toJSON().toString());
                el.setSenderSubjectId(sender.getId());
                eventLogDao.create(el);
            }
        }

        //save in the logs what kinds of events?

        messagingTemplate.convertAndSend("/topic/public/task/" + completedTaskId + "/communication", pogsMessage);
    }
}
