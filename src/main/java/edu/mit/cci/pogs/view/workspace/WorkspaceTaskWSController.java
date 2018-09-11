package edu.mit.cci.pogs.view.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.util.Date;

import edu.mit.cci.pogs.messages.PogsMessage;
import edu.mit.cci.pogs.messages.TaskAttributeMessage;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.service.CompletedTaskAttributeService;

@Controller
public class WorkspaceTaskWSController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private CompletedTaskAttributeService completedTaskAttributeService;

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private SubjectDao subjectDao;

    @MessageMapping("/task.saveAttribute")
    public void getLoggableAttribute(@Payload TaskAttributeMessage taskAttributeMessage) {
        if(taskAttributeMessage.getType().equals(PogsMessage.MessageType.TASK_ATTRIBUTE)) {
            String externalId = taskAttributeMessage.getSender();
            Long completedTaskId = Long.parseLong(taskAttributeMessage.getCompletedTaskId());
            Long sessionId = Long.parseLong(taskAttributeMessage.getSessionId());


            if(taskAttributeMessage.getLoggableAttribute()){

                completedTaskAttributeService.createOrUpdate(
                        taskAttributeMessage.getAttributeName(),
                        taskAttributeMessage.getAttributeStringValue(),
                        taskAttributeMessage.getAttributeDoubleValue(),
                        taskAttributeMessage.getAttributeIntegerValue(),
                        Long.parseLong(taskAttributeMessage.getCompletedTaskId())
                );
                Subject sender = subjectDao.getByExternalId(taskAttributeMessage.getSender());
                if(sender!=null) {
                    EventLog el = new EventLog();
                    el.setCompletedTaskId(completedTaskId);
                    el.setSessionId(sessionId);
                    el.setSender(taskAttributeMessage.getSender());
                    el.setReceiver(taskAttributeMessage.getReceiver());
                    el.setTimestamp(new Timestamp(new Date().getTime()));
                    el.setEventType(taskAttributeMessage.getType().name().toString());
                    el.setEventContent(taskAttributeMessage.toJSON().toString());
                    el.setSenderSubjectId(sender.getId());
                    eventLogDao.create(el);
                }

            }


            messagingTemplate.convertAndSend("/topic/public/task/"+completedTaskId + "/work", taskAttributeMessage);
        }
    }
}
