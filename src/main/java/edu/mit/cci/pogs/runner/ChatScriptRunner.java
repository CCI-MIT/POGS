package edu.mit.cci.pogs.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import edu.mit.cci.pogs.messages.CommunicationMessage;
import edu.mit.cci.pogs.messages.CommunicationMessageContent;
import edu.mit.cci.pogs.messages.PogsMessage;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;

@Component
public class ChatScriptRunner implements Runnable {


    private List<ChatEntry> chatEntryList;


    private TaskWrapper taskWrapper;

    private SessionWrapper session;

    private static final Logger _log = LoggerFactory.getLogger(ChatScriptRunner.class);


    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Override
    public void run() {

        Long timeBeforeStarts = taskWrapper.getPrimerEndTime() - new Date().getTime();
        _log.info("Starting chatScript for task: " + taskWrapper.getId());
        try {
            if (timeBeforeStarts > 0) {
                _log.debug("Sleeping before sending, for: " + timeBeforeStarts);
                Thread.sleep(timeBeforeStarts);
            }
            Long lastTimeDiff = 0l;
            for(ChatEntry ce: chatEntryList) {
                Thread.sleep((ce.getChatElapsedTime() - lastTimeDiff)*1000);
                lastTimeDiff =  ce.getChatElapsedTime();
                createAndSendMessage(ce);
            }

        } catch (InterruptedException ie) {
            _log.info("Stopping chat script for task : " + taskWrapper.getId());
        }

    }

    private void createAndSendMessage(ChatEntry ce) {
        CommunicationMessageContent cmc = new CommunicationMessageContent();
        cmc.setType(CommunicationMessage.CommunicationType.MESSAGE);
        cmc.setMessage(ce.getChatEntryValue());
        for(CompletedTask ct: taskWrapper.getCompletedTasks()) {
            CommunicationMessage pogsMessage = new CommunicationMessage();
            pogsMessage.setType(PogsMessage.MessageType.COMMUNICATION_MESSAGE);
            pogsMessage.setSender(session.getChatBotName());
            pogsMessage.setReceiver(null);
            pogsMessage.setSessionId(session.getId().toString());
            pogsMessage.setCompletedTaskId(ct.getId().toString());
            pogsMessage.setContent(cmc);
            _log.debug("Sending message: " + pogsMessage.getContent().getMessage() + " to completed task: " + ct.getId());
            messagingTemplate.convertAndSend("/topic/public/task/" + ct.getId() + "/communication", pogsMessage);
        }
    }

    public List<ChatEntry> getChatEntryList() {
        return chatEntryList;
    }

    public void setChatEntryList(List<ChatEntry> chatEntryList) {
        this.chatEntryList = chatEntryList;
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
