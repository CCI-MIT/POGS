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
import edu.mit.cci.pogs.service.SubjectHasSessionCheckInService;

@Controller
public class WorkspaceCheckinWSController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private SubjectHasSessionCheckInService subjectHasSessionCheckInService;


    @MessageMapping("/checkIn.sendMessage")
    public void getCheckin(@Payload CheckInMessage pogsMessage, SimpMessageHeaderAccessor headerAccessor) {
        Long completedTaskId = null;
        if (pogsMessage.getCompletedTaskId() != null) {
            completedTaskId = Long.parseLong(pogsMessage.getCompletedTaskId());
//            if(completedTaskId == 0){
//                completedTaskId = 0l;
//            }

        }
        Long sessionId = Long.parseLong(pogsMessage.getSessionId());

        if (pogsMessage.getContent().getType().equals(CommunicationMessage.CommunicationType.CHECK_IN.name())) {
            headerAccessor.getSessionAttributes().put("externalUserId", pogsMessage.getSender());
        }


        //save only first checkin to database
        if (!pogsMessage.getContent().getChannel().isEmpty() && pogsMessage.getContent().getChannel().equals("true")) {
            Subject sender = subjectDao.getByExternalId(pogsMessage.getSender());
            EventLog el = new EventLog();
            el.setCompletedTaskId(((completedTaskId==null) ||(completedTaskId==0))?(null):(completedTaskId));
            el.setSessionId(sessionId);
            el.setSender(pogsMessage.getSender());
            el.setReceiver(pogsMessage.getReceiver());
            el.setTimestamp(new Timestamp(new Date().getTime()));
            el.setEventType(pogsMessage.getType().name().toString());
            el.setEventContent(pogsMessage.toJSON().toString());
            if (sender != null) {//handle non users (task preview)
                el.setSenderSubjectId(sender.getId());
            }
            el.setExtraData("");
            el.setSummaryDescription("Subject loaded : " + pogsMessage.getContent().getMessage());
            //&& completedTaskId!=null && completedTaskId > 0


            if (sender != null&& (completedTaskId!=null && (completedTaskId != -1)) ) {

                eventLogDao.create(el);
                if (pogsMessage.getContent().getMessage().contains("/start/")) {
                    subjectHasSessionCheckInService.updateLatestSubjectPing(sender.getId(), sessionId);
                }
            }
        }

        if (pogsMessage.getContent().getMessage().contains("/start/")) {
            Subject sender = subjectDao.getByExternalId(pogsMessage.getSender());
            if (sender != null) {
                subjectHasSessionCheckInService.updateLatestSubjectPing(sender.getId(), sessionId);
            }
        }

        //save in the logs what kinds of events?

        messagingTemplate.convertAndSend("/topic/public/checkin/" + sessionId, pogsMessage);
    }
}
