package edu.mit.cci.pogs.view.workspace;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.mit.cci.pogs.messages.CollaborationMessage;
import edu.mit.cci.pogs.messages.CollaborationMessageContent;
import edu.mit.cci.pogs.messages.PogsMessage;
import edu.mit.cci.pogs.messages.TodoListCollaborationMessage;
import edu.mit.cci.pogs.messages.TodoListMessageContent;
import edu.mit.cci.pogs.messages.VotingPoolCollaborationMessage;
import edu.mit.cci.pogs.messages.VotingPoolMessageContent;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.todoentry.TodoEntryDao;
import edu.mit.cci.pogs.model.dao.todoentryassignment.TodoEntryAssignmentDao;
import edu.mit.cci.pogs.model.dao.votingpool.VotingPoolDao;
import edu.mit.cci.pogs.model.dao.votingpooloption.VotingPoolOptionDao;
import edu.mit.cci.pogs.model.dao.votingpoolvote.VotingPoolVoteDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntryAssignment;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPool;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolOption;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolVote;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.SessionRunnerManager;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.service.TodoEntryService;
import edu.mit.cci.pogs.service.VotingService;


@Controller
public class WorkspaceCollaborationWSController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private TodoEntryAssignmentDao todoEntryAssignmentDao;

    @Autowired
    private TodoEntryDao todoEntryDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private VotingPoolDao votingPoolDao;

    @Autowired
    private VotingPoolOptionDao votingPoolOptionDao;

    @Autowired
    private VotingPoolVoteDao votingPoolVoteDao;

    @Autowired
    private VotingService votingService;

    @Autowired
    private EventLogDao eventLogDao;

    @MessageMapping("/collaboration.sendMessage")
    public void sendCollaborationMessage(@Payload CollaborationMessage pogsMessage) {


        if (pogsMessage.getContent().getCollaborationType().equals(CollaborationMessage.CollaborationType.TODO_LIST)) {
            handleTodoListMessage(pogsMessage);
            return;
        }

        if (pogsMessage.getContent().getCollaborationType().equals(CollaborationMessage.CollaborationType.VOTING_LIST)) {
            handleVotingPoolMessage(pogsMessage);
            return;
        }
    }

    private void createLogEntry(Long completedTaskId, Long sessionId, CollaborationMessage pogsMessage, Subject sender, String content) {
        EventLog el = new EventLog();
        el.setCompletedTaskId(completedTaskId);
        el.setSessionId(sessionId);
        el.setSender(pogsMessage.getSender());
        el.setReceiver(pogsMessage.getReceiver());
        el.setTimestamp(new Timestamp(new Date().getTime()));
        el.setEventType(pogsMessage.getType().name().toString());
        el.setEventContent(content);
        el.setSenderSubjectId(sender.getId());
        eventLogDao.create(el);
    }

    private void handleVotingPoolMessage(CollaborationMessage pogsMessage) {

        Long completedTaskId = Long.parseLong(pogsMessage.getCompletedTaskId());
        Long sessionId = Long.parseLong(pogsMessage.getSessionId());
        SessionRunner sessionWrapper = SessionRunnerManager.getSessionRunner(sessionId);
        Subject subject = subjectDao.getByExternalId(pogsMessage.getSender());

        CollaborationMessage.VotingPoolType todoType =
                CollaborationMessage.VotingPoolType.getType(pogsMessage.getContent()
                        .getMessageType());
        VotingPoolCollaborationMessage allVotingOptions = new VotingPoolCollaborationMessage();
        allVotingOptions.setType(PogsMessage.MessageType.COLLABORATION_MESSAGE);
        allVotingOptions.setCompletedTaskId(completedTaskId.toString());
        VotingPoolMessageContent vpmc = new VotingPoolMessageContent();


        vpmc.setCollaborationType(CollaborationMessage.CollaborationType.VOTING_LIST);

        vpmc.setMessageType(CollaborationMessage.VotingPoolType.
                BROADCAST_VOTING_POOLS.name().toString());
        vpmc.setTriggeredBy(pogsMessage.getContent().getMessageType());
        if (todoType.equals(CollaborationMessage.VotingPoolType.CREATE_VOTING_POOL)) {
            VotingPool vp = new VotingPool();
            vp.setCompletedTaskId(completedTaskId);
            vp.setVotingQuestion(pogsMessage.getContent().getMessage());
            votingPoolDao.create(vp);
        }
        if (todoType.equals(CollaborationMessage.VotingPoolType.CREATE_OPTION)) {
            String[] message = pogsMessage.getContent().getMessage().split("<separator>");
            VotingPoolOption vpo = new VotingPoolOption();
            vpo.setVotingOption(message[0]);
            vpo.setVotingPoolId(Long.parseLong(message[1]));
            votingPoolOptionDao.create(vpo);
        }
        if (todoType.equals(CollaborationMessage.VotingPoolType.DELETE_VOTING_POOL)) {
            Long votingPoolId = Long.parseLong(pogsMessage.getContent().getMessage());
            votingService.deleteVotingPool(votingPoolId);
            vpmc.setTriggeredData(votingPoolId.toString());
        }
        if (todoType.equals(CollaborationMessage.VotingPoolType.DELETE_OPTION)) {
            Long votingPoolOptionId = Long.parseLong(pogsMessage.getContent().getMessage());
            votingService.deleteVotingPoolOption(votingPoolOptionId);
            vpmc.setTriggeredData(votingPoolOptionId.toString());
        }
        if (todoType.equals(CollaborationMessage.VotingPoolType.CAST_VOTE)) {

            Long votingPoolId = Long.parseLong(pogsMessage.getContent().getMessage());
            VotingPoolVote vpv = votingPoolVoteDao.getBySubjectId(subject.getId());
            if (vpv != null) {
                vpv.setVotingPoolOptionId(votingPoolId);
                votingPoolVoteDao.update(vpv);
            } else {
                vpv = new VotingPoolVote();
                vpv.setVotingPoolOptionId(votingPoolId);
                vpv.setSubjectId(subject.getId());
                votingPoolVoteDao.create(vpv);
            }
        }
        List<VotingPool> votingPools = votingPoolDao.listByCompletedTaskId(completedTaskId);
        if (votingPools != null) {
            Long totalOfVotes = 0l;
            for (VotingPool vp : votingPools) {
                List<VotingPoolOption> votingPoolOptions =
                        votingPoolOptionDao.listByVotingPoolId(vp.getId());
                List<Integer> optionVotes = new ArrayList<>();
                for (VotingPoolOption vpo : votingPoolOptions) {
                    Integer vote = votingPoolVoteDao.countVote(vpo.getId());
                    totalOfVotes += vote;
                    optionVotes.add(vote);
                }
                vpmc.addVotingPool(vp, votingPoolOptions, optionVotes, totalOfVotes);
            }
        }

        allVotingOptions.setContent(vpmc);


        JSONObject jo = new JSONObject();
        jo.put("collaborationType", pogsMessage.getContent().getCollaborationType());
        jo.put("messageType", pogsMessage.getContent().getMessageType());
        jo.put("message", pogsMessage.getContent().getMessage());
        JSONObject listContent = allVotingOptions.getContent().toJSON();

        if (listContent != null) {
            Iterator<String> it = listContent.keySet().iterator();
            while (it.hasNext()) {
                String prop = it.next();
                jo.put(prop, listContent.get(prop));
            }
        }

        createLogEntry(completedTaskId, sessionId, pogsMessage, subject, jo.toString());

        messagingTemplate.convertAndSend("/topic/public/task/" + pogsMessage.getCompletedTaskId() + "/collaboration", allVotingOptions);
    }

    private void handleTodoListMessage(CollaborationMessage pogsMessage) {

        Long completedTaskId = Long.parseLong(pogsMessage.getCompletedTaskId());
        Long sessionId = Long.parseLong(pogsMessage.getSessionId());

        SessionRunner sessionRunner = SessionRunnerManager.getSessionRunner(sessionId);

        TodoListCollaborationMessage allTodoEntriesMessage = new TodoListCollaborationMessage();
        allTodoEntriesMessage.setType(PogsMessage.MessageType.COLLABORATION_MESSAGE);
        allTodoEntriesMessage.setCompletedTaskId(completedTaskId.toString());
        TodoListMessageContent cmc = new TodoListMessageContent();
        cmc.setCollaborationType(CollaborationMessage.CollaborationType.TODO_LIST);
        cmc.setMessageType(CollaborationMessage.TodoType.BROADCAST_TODO_ITEMS.name().toString());

        if(sessionRunner!=null) {

            Map<String, Subject> subjectMap = sessionRunner.getAllCheckedInSubjects();
            CollaborationMessage.TodoType todoType = CollaborationMessage
                    .TodoType.getType(pogsMessage.getContent().getMessageType());

            Subject su = subjectMap.get(pogsMessage.getSender());


            allTodoEntriesMessage.setContent(cmc);
            cmc.setTriggeredBy(pogsMessage.getContent().getMessageType());


            if (todoType.equals(CollaborationMessage.TodoType.CREATE_TODO)) {
                //do what it needs to do to create the todo
                TodoEntry te = new TodoEntry();
                te.setCompletedTaskId(completedTaskId);

                te.setCreatorId(su.getId());
                te.setText(pogsMessage.getContent().getMessage());
                te.setMarkedDone(false);
                te.setTodoEntryDate(new Timestamp(new Date().getTime()));
                todoEntryDao.create(te);

            }
            if (todoType.equals(CollaborationMessage.TodoType.DELETE_TODO)) {
                Long todoEntryId = Long.parseLong(pogsMessage.getContent().getMessage());
                TodoEntry te = todoEntryDao.get(todoEntryId);
                te.setDeletedAt(new Timestamp(new Date().getTime()));
                todoEntryDao.update(te);
                cmc.setTriggeredData(todoEntryId.toString());

            }
            if (todoType.equals(CollaborationMessage.TodoType.ASSIGN_ME)) {
                Long todoEntry = Long.parseLong(pogsMessage.getContent().getMessage());

                TodoEntry te = todoEntryDao.get(todoEntry);


                TodoEntryAssignment tea = todoEntryAssignmentDao.getByTodoEntryIdSubjectId(todoEntry, su.getId());
                if (tea != null) {
                    tea.setCurrentAssigned(true);
                    todoEntryAssignmentDao.update(tea);
                } else {
                    tea = new TodoEntryAssignment();
                    tea.setCurrentAssigned(true);
                    tea.setSubjectId(su.getId());
                    tea.setTodoEntryId(te.getId());
                    tea.setAssignmentDate(new Timestamp(new Date().getTime()));
                    todoEntryAssignmentDao.create(tea);
                }


            }
            if (todoType.equals(CollaborationMessage.TodoType.UNASSIGN_ME)) {
                Long todoEntry = Long.parseLong(pogsMessage.getContent().getMessage());

                TodoEntry te = todoEntryDao.get(todoEntry);

                TodoEntryAssignment tea = todoEntryAssignmentDao.getByTodoEntryIdSubjectId(todoEntry, su.getId());
                if (tea != null) {
                    tea.setCurrentAssigned(false);
                    todoEntryAssignmentDao.update(tea);
                }
                cmc.setTriggeredData(todoEntry.toString());
                cmc.setTriggeredData2(su.getSubjectExternalId());

            }
            if (todoType.equals(CollaborationMessage.TodoType.MARK_DONE)) {
                Long todoEntry = Long.parseLong(pogsMessage.getContent().getMessage());
                TodoEntry te = todoEntryDao.get(todoEntry);
                if (te != null) {
                    te.setMarkedDone(true);
                    te.setMarkedDoneDate(new Timestamp(new Date().getTime()));
                    todoEntryDao.update(te);
                }

            }
            if (todoType.equals(CollaborationMessage.TodoType.MARK_UNDONE)) {
                Long todoEntry = Long.parseLong(pogsMessage.getContent().getMessage());
                TodoEntry te = todoEntryDao.get(todoEntry);
                if (te != null) {
                    te.setMarkedDone(false);
                    te.setMarkedDoneDate(null);
                    todoEntryDao.update(te);
                }
            }


            List<TodoEntry> allTodoEntries = todoEntryDao.listByCompletedTaskId(completedTaskId);//there should be a method to get from completed ID
            for (TodoEntry te : allTodoEntries) {
                List<TodoEntryAssignment> todoEntryAssignmentList = todoEntryAssignmentDao.listByTodoEntryId(te.getId(), true);
                List<String> assignedSubjects = new ArrayList<>();
                if (todoEntryAssignmentList != null) {
                    for (TodoEntryAssignment tea : todoEntryAssignmentList) {

                        su = null;
                        for (Subject as : subjectMap.values()) {
                            if (as.getId() == tea.getSubjectId()) {
                                su = as;
                                break;
                            }
                        }
                        if (su != null) {
                            assignedSubjects.add(su.getSubjectExternalId());
                        }
                    }
                }
                cmc.addTodoEntry(te.getId(), te.getText(), te.getMarkedDone(), assignedSubjects);

            }

            JSONObject jo = new JSONObject();
            jo.put("collaborationType", pogsMessage.getContent().getCollaborationType());
            jo.put("messageType", pogsMessage.getContent().getMessageType());
            jo.put("message", pogsMessage.getContent().getMessage());
            JSONObject listContent = allTodoEntriesMessage.getContent().toJSON();
            if (listContent != null) {
                Iterator<String> it = listContent.keySet().iterator();
                while (it.hasNext()) {
                    String prop = it.next();
                    jo.put(prop, listContent.get(prop));
                }
            }


            createLogEntry(completedTaskId, sessionId, pogsMessage, su, jo.toString());
        }
        messagingTemplate.convertAndSend("/topic/public/task/" + pogsMessage.getCompletedTaskId() + "/collaboration", allTodoEntriesMessage);
    }
}
