package edu.mit.cci.pogs.view.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import edu.mit.cci.pogs.messages.PogsMessage;
import edu.mit.cci.pogs.messages.TaskAttributeMessage;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.SessionRunnerManager;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
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
    public void saveTaskAttribute(@Payload TaskAttributeMessage taskAttributeMessage) {
        if(taskAttributeMessage.getType().equals(PogsMessage.MessageType.TASK_ATTRIBUTE)) {
            String externalId = taskAttributeMessage.getSender();
            Long completedTaskId = Long.parseLong(taskAttributeMessage.getCompletedTaskId());
            Long sessionId = Long.parseLong(taskAttributeMessage.getSessionId());
            SessionRunner sr = SessionRunnerManager.getSessionRunner(sessionId);


            if(sr!=null) {
                if (taskAttributeMessage.getLoggableAttribute()) {

                    sr.getSession().addSubjectContribution(completedTaskId, externalId, 1);


                    completedTaskAttributeService.createOrUpdate(
                            taskAttributeMessage.getAttributeName(),
                            taskAttributeMessage.getAttributeStringValue(),
                            taskAttributeMessage.getAttributeDoubleValue(),
                            taskAttributeMessage.getAttributeIntegerValue(),
                            Long.parseLong(taskAttributeMessage.getCompletedTaskId()),
                            taskAttributeMessage.getExtraData(),
                            taskAttributeMessage.getMustCreateNewAttribute()
                    );
                    Subject sender = subjectDao.getByExternalId(taskAttributeMessage.getSender());
                    if (sender != null) {
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
            } else{
                //handle in memory event log for testing

            }

            if(taskAttributeMessage.getBroadcastableAttribute()) {
                messagingTemplate.convertAndSend("/topic/public/task/" + completedTaskId + "/work", taskAttributeMessage);
            }
        }
    }
}
