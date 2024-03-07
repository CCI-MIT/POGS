package edu.mit.cci.pogs.runner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.Date;

import edu.mit.cci.pogs.messages.CollaborationMessage;
import edu.mit.cci.pogs.messages.CollaborationMessageContent;
import edu.mit.cci.pogs.messages.CommunicationMessage;
import edu.mit.cci.pogs.messages.CommunicationMessageContent;
import edu.mit.cci.pogs.messages.PogsMessage;
import edu.mit.cci.pogs.messages.TaskAttributeMessage;
import edu.mit.cci.pogs.messages.TaskAttributeMessageContent;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.view.workspace.WorkspaceCollaborationWSController;
import edu.mit.cci.pogs.view.workspace.WorkspaceCommunicationWSController;
import edu.mit.cci.pogs.view.workspace.WorkspaceTaskWSController;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskEventReplayRunner implements Runnable {

    private JSONObject sessionEvents;

    private TaskWrapper taskWrapper;

    private SessionWrapper session;


    @Autowired
    private WorkspaceTaskWSController taskWSController;

    @Autowired
    private WorkspaceCommunicationWSController communicationWSController;

    @Autowired
    private WorkspaceCollaborationWSController collaborationWSController;


    private static final Logger _log = LoggerFactory.getLogger(TaskEventReplayRunner.class);


    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Override
    public void run() {

        Long timeBeforeStarts = taskWrapper.getPrimerEndTime() - new Date().getTime();
        _log.info("Starting taskEventReplay for task: " + taskWrapper.getId());
        try {
            if (timeBeforeStarts > 0) {
                _log.debug("Sleeping before sending, for: " + timeBeforeStarts);
                Thread.sleep(timeBeforeStarts);
            }
            _log.debug("Sending replay entries for: " + timeBeforeStarts);
            Long lastTimeDiff = 0l;

            JSONArray taskEvents = this.sessionEvents.getJSONArray(this.taskWrapper.getId().toString());
            if(taskEvents!=null) {
                for(int j=0; j < taskEvents.length(); j ++) {
                    JSONObject event = taskEvents.getJSONObject(j);
                    Long eventTime = event.getLong("timestamp");
                    Thread.sleep((eventTime - lastTimeDiff));
                    lastTimeDiff = eventTime;
                    createAndSendMessage(event.getJSONObject("rawEventData"));
                }
            }


        } catch (InterruptedException ie) {
            _log.info("Stopping taskEventReplay for task : " + taskWrapper.getId());
        }

    }

    private void createAndSendMessage(JSONObject ce) {



        if(ce.getString("type").equals("TASK_ATTRIBUTE")){
            JSONObject content = new JSONObject(ce.getString("content"));
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

            tam.setSender(ce.getString("sender"));
            tam.setReceiver((ce.isNull("receiver"))?(null):(ce.getString("receiver")));
            tam.setContent(tamc);
            tam.setType(TaskAttributeMessage.MessageType.TASK_ATTRIBUTE);
            tam.setSessionId(session.getId().toString());



            for(CompletedTask ct: taskWrapper.getCompletedTasks()) {
                tam.setCompletedTaskId(ct.getId().toString());
                //messagingTemplate.convertAndSend("/topic/public/task/" + ct.getId() + "/communication", pogsMessage);
                taskWSController.saveTaskAttribute(tam);
            }
        }
        if(ce.getString("type").equals("COMMUNICATION_MESSAGE")){
            JSONObject content = new JSONObject(ce.getString("content"));
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

            pogsMessage.setSender(ce.getString("sender"));
            pogsMessage.setReceiver((ce.isNull("receiver"))?(null):(ce.getString("receiver")));


            pogsMessage.setSessionId(session.getId().toString());

            pogsMessage.setContent(cmc);
            for(CompletedTask ct: taskWrapper.getCompletedTasks()) {
                pogsMessage.setCompletedTaskId(ct.getId().toString());
                communicationWSController.sendMessage(pogsMessage,null);
            }

        }
        if(ce.getString("type").equals("COLLABORATION_MESSAGE")){
            JSONObject content = new JSONObject(ce.getString("content"));
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
            cm.setSender(ce.getString("sender"));
            cm.setReceiver((ce.isNull("receiver"))?(null):(ce.getString("receiver")));

            cm.setContent(cmc);

            cm.setSessionId(session.getId().toString());
            for(CompletedTask ct: taskWrapper.getCompletedTasks()) {
                cm.setCompletedTaskId(ct.getId().toString());
                collaborationWSController.sendCollaborationMessage(cm);
            }

        }


    }

    public JSONObject getSessionEvents() {
        return sessionEvents;
    }

    public void setSessionEvents(JSONObject sessionEvents) {
        this.sessionEvents = sessionEvents;
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


}
