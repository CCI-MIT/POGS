package edu.mit.cci.pogs.runner;

import edu.mit.cci.pogs.messages.*;
import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.round.RoundDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.service.CompletedTaskService;
import edu.mit.cci.pogs.service.EventLogService;
import edu.mit.cci.pogs.utils.DateUtils;
import edu.mit.cci.pogs.view.workspace.WorkspaceCollaborationWSController;
import edu.mit.cci.pogs.view.workspace.WorkspaceCommunicationWSController;
import edu.mit.cci.pogs.view.workspace.WorkspaceTaskWSController;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PaginatedTaskEventReplayRunner implements Runnable {

    private Long sourceSessionId;

    private TaskWrapper taskWrapper;

    private SessionWrapper session;

    @Autowired
    private WorkspaceTaskWSController taskWSController;

    @Autowired
    private WorkspaceCommunicationWSController communicationWSController;

    @Autowired
    private WorkspaceCollaborationWSController collaborationWSController;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    @Autowired
    private RoundDao roundDao;

    @Autowired
    private EventLogService eventLogService;

    private static final Logger _log = LoggerFactory.getLogger(PaginatedTaskEventReplayRunner.class);

    private static final Integer EVENTS_PER_PAGE = 1000;
    @Override
    public void run() {

        Long timeBeforeStarts = taskWrapper.getPrimerEndTime() - new Date().getTime();
        List<Round> roundId = roundDao.listBySessionId(sourceSessionId);
        CompletedTask ct = completedTaskDao.getByRoundIdTaskIdTeamId(roundId.get(0).getId(), null, taskWrapper.getId());
        if(ct == null) {
            return;
        }
        _log.info("Starting taskEventReplay for task: " + taskWrapper.getId());
        try {
            if (timeBeforeStarts > 0) {
                _log.debug("Sleeping before sending, for: " + timeBeforeStarts);
                Thread.sleep(timeBeforeStarts);
            }
            _log.debug("Sending replay entries for: " + timeBeforeStarts);

            Integer totalEvents = eventLogService.countEventsBySessionIdCompletedTaskIdTotal(sourceSessionId,ct.getId());
            Integer totalPages = (totalEvents/EVENTS_PER_PAGE);
            CompletedTask currentCompletedTask = taskWrapper.getCompletedTasks().get(0);
            long startTime = currentCompletedTask.getStartTime().getTime();
            for(int page =0 ; page < totalPages; page++){
                List<EventLog> events = eventLogService.getEventsBySessionIdCompletedTaskIdTotal(sourceSessionId, ct.getId(),
                        page*EVENTS_PER_PAGE, EVENTS_PER_PAGE);

                for(EventLog e: events) {
                    Long eventTime = e.getTimestamp().getTime() - ct.getStartTime().getTime();
                    if( ((eventTime + startTime) - DateUtils.now()) >0 ) {
                        Thread.sleep((eventTime + startTime)- DateUtils.now());
                    }
                    createAndSendMessage(e);
                }
            }

        }catch (InterruptedException ie) {

        }
    }

    private void createAndSendMessage(EventLog ce) {
        if(ce.getEventType().equals("TASK_ATTRIBUTE")){
            JSONObject content = new JSONObject(ce.getEventContent());
            TaskAttributeMessageContent tamc = new TaskAttributeMessageContent();
            if(content.has("attributeName"))
                tamc.setAttributeName(content.getString("attributeName"));
            if(content.has("attributeStringValue"))
                tamc.setAttributeStringValue(content.getString("attributeStringValue"));
            if(content.has("attributeDoubleValue"))
                tamc.setAttributeDoubleValue(content.getDouble("attributeDoubleValue"));
            if(content.has("attributeIntegerValue"))
                tamc.setAttributeIntegerValue(content.getLong("attributeIntegerValue"));
            if(content.has("loggableAttribute"))
                tamc.setLoggableAttribute(content.getBoolean("loggableAttribute"));
            if(content.has("summaryDescription"))

                tamc.setSummaryDescription((content.isNull("summaryDescription"))?
                        (null):("Robot " + content.getString("summaryDescription")));

            if(content.has("extraData"))
                tamc.setExtraData(content.getString("extraData"));
            if(content.has("mustCreateNewAttribute"))
                tamc.setMustCreateNewAttribute(content.getBoolean("mustCreateNewAttribute"));
            TaskAttributeMessage tam = new TaskAttributeMessage();

            tam.setSender(ce.getSender());
            tam.setReceiver((ce.getReceiver()==null)?(null):(ce.getReceiver()));
            tam.setContent(tamc);
            tam.setType(TaskAttributeMessage.MessageType.TASK_ATTRIBUTE);
            tam.setSessionId(session.getId().toString());
            for(CompletedTask ct: taskWrapper.getCompletedTasks()) {
                tam.setCompletedTaskId(ct.getId().toString());
                //messagingTemplate.convertAndSend("/topic/public/task/" + ct.getId() + "/communication", pogsMessage);
                taskWSController.saveTaskAttribute(tam);
            }
        }
        if(ce.getEventType().equals("COMMUNICATION_MESSAGE")){
            JSONObject content = new JSONObject(ce.getEventContent());
            CommunicationMessageContent cmc = new CommunicationMessageContent();

            if(content.has("type")){
                cmc.setType(
                        CommunicationMessage.CommunicationType.
                                getByString(content.getString("type"))
                );
            }
            if(content.has("message"))
                cmc.setMessage(content.getString("message"));
            if(content.has("channel"))
                cmc.setChannel(content.getString("channel"));


            CommunicationMessage pogsMessage = new CommunicationMessage();

            pogsMessage.setType(PogsMessage.MessageType.COMMUNICATION_MESSAGE);

            pogsMessage.setSender(ce.getSender());
            pogsMessage.setReceiver((ce.getReceiver()==null)?(null):(ce.getReceiver()));


            pogsMessage.setSessionId(session.getId().toString());

            pogsMessage.setContent(cmc);
            for(CompletedTask ct: taskWrapper.getCompletedTasks()) {
                pogsMessage.setCompletedTaskId(ct.getId().toString());
                communicationWSController.sendMessage(pogsMessage,null);
            }

        }
        if(ce.getEventType().equals("COLLABORATION_MESSAGE")){
            JSONObject content = new JSONObject(ce.getEventContent());
            CollaborationMessageContent cmc = new CollaborationMessageContent();


            if(content.has("collaborationType")) {
                cmc.setCollaborationType(
                        CollaborationMessage.CollaborationType.getByString(
                                content.getString("collaborationType")
                        ));
            }
            if(content.has("messageType")) {
                cmc.setMessageType(content.getString("messageType"));
            }
            if(content.has("message")){
                cmc.setMessage(content.getString("message"));
            }

            CollaborationMessage cm = new CollaborationMessage();

            cm.setType(PogsMessage.MessageType.COLLABORATION_MESSAGE);
            cm.setSender(ce.getSender());
            cm.setReceiver((ce.getReceiver()==null)?(null):(ce.getReceiver()));

            cm.setContent(cmc);

            cm.setSessionId(session.getId().toString());
            for(CompletedTask ct: taskWrapper.getCompletedTasks()) {
                cm.setCompletedTaskId(ct.getId().toString());
                collaborationWSController.sendCollaborationMessage(cm);
            }

        }
    }

    public Long getSourceSessionId() {
        return sourceSessionId;
    }

    public void setSourceSessionId(Long sourceSessionId) {
        this.sourceSessionId = sourceSessionId;
    }

    public TaskWrapper getTaskWrapper() {
        return taskWrapper;
    }

    public void setTaskWrapper(TaskWrapper taskWrapper) {
        this.taskWrapper = taskWrapper;
    }

    public SessionWrapper getSession() {
        return session;
    }

    public void setSession(SessionWrapper session) {
        this.session = session;
    }

    public WorkspaceTaskWSController getTaskWSController() {
        return taskWSController;
    }

    public void setTaskWSController(WorkspaceTaskWSController taskWSController) {
        this.taskWSController = taskWSController;
    }

    public WorkspaceCommunicationWSController getCommunicationWSController() {
        return communicationWSController;
    }

    public void setCommunicationWSController(WorkspaceCommunicationWSController communicationWSController) {
        this.communicationWSController = communicationWSController;
    }

    public WorkspaceCollaborationWSController getCollaborationWSController() {
        return collaborationWSController;
    }

    public void setCollaborationWSController(WorkspaceCollaborationWSController collaborationWSController) {
        this.collaborationWSController = collaborationWSController;
    }

    public CompletedTaskDao getCompletedTaskDao() {
        return completedTaskDao;
    }

    public void setCompletedTaskDao(CompletedTaskDao completedTaskDao) {
        this.completedTaskDao = completedTaskDao;
    }

    public RoundDao getRoundDao() {
        return roundDao;
    }

    public void setRoundDao(RoundDao roundDao) {
        this.roundDao = roundDao;
    }

    public EventLogService getEventLogService() {
        return eventLogService;
    }

    public void setEventLogService(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }
}
