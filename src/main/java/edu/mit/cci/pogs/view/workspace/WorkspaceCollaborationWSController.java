package edu.mit.cci.pogs.view.workspace;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.List;

import edu.mit.cci.pogs.messages.CollaborationMessage;
import edu.mit.cci.pogs.messages.CollaborationMessageContent;
import edu.mit.cci.pogs.model.dao.todoentry.TodoEntryDao;
import edu.mit.cci.pogs.model.dao.todoentryassignment.TodoEntryAssignmentDao;
import edu.mit.cci.pogs.model.dao.votingpool.VotingPoolDao;
import edu.mit.cci.pogs.model.dao.votingpooloption.VotingPoolOptionDao;
import edu.mit.cci.pogs.model.dao.votingpoolvote.VotingPoolVoteDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntryAssignment;


@Controller
public class WorkspaceCollaborationWSController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private TodoEntryAssignmentDao todoEntryAssignmentDao;

    @Autowired
    private TodoEntryDao todoEntryDao;


    @Autowired
    private VotingPoolDao votingPoolDao;

    @Autowired
    private VotingPoolOptionDao votingPoolOptionDao;

    @Autowired
    private VotingPoolVoteDao votingPoolVoteDao;

    @MessageMapping("/collaboration.sendMessage")
    public void getCheckin(@Payload CollaborationMessage pogsMessage) {


        
        if(pogsMessage.getContent().getCollaborationType().equals(CollaborationMessage.CollaborationType.TODO_LIST)){
            handleTodoListMessage(pogsMessage);
            return;
        }

        if(pogsMessage.getContent().getCollaborationType().equals(CollaborationMessage.CollaborationType.VOTING_LIST)){
            handleVotingPoolMessage(pogsMessage);
            return;
        }
    }

    private void handleVotingPoolMessage(CollaborationMessage pogsMessage) {

    }

    private void handleTodoListMessage(CollaborationMessage pogsMessage) {

        CollaborationMessage.TodoType todoType = CollaborationMessage.TodoType.getType(pogsMessage.getContent().getMessageType());
        if(todoType.equals(CollaborationMessage.TodoType.CREATE_TODO)){
            //do what it needs to do to create the todo
        }

        CollaborationMessage allTodoEntriesMessage = new CollaborationMessage();
        CollaborationMessageContent cmc = new CollaborationMessageContent();
        cmc.setCollaborationType(CollaborationMessage.CollaborationType.TODO_LIST);
        cmc.setMessageType(CollaborationMessage.TodoType.BROADCAST_TODO_ITEMS.name().toString());
        allTodoEntriesMessage.setContent(cmc);

        JSONObject broadCastTodoMessage = new JSONObject();
        broadCastTodoMessage.put("triggeredBy", pogsMessage.getContent().getMessageType());
        //this may be usefull to avoid refreshing all content

        JSONArray todoLists = new JSONArray();
        List<TodoEntry> allTodoEntries = todoEntryDao.list();//there should be a method to get from completed ID
        for(TodoEntry te: allTodoEntries){
            //todoEntryAssignmentDao.list()
            JSONObject entry = new JSONObject();
            entry.put("entryId", te.getId());
            entry.put("entryText", te.getText());
            entry.put("entryMarkedDone", te.getMarkedDone());

            todoLists.add(entry);
        }
        broadCastTodoMessage.put("todoEntries", todoLists);

        cmc.setMessage(broadCastTodoMessage.toString());

        messagingTemplate.convertAndSend("/topic/public/task/"+pogsMessage.getCompletedTaskId() + "/collaboration", allTodoEntriesMessage);
    }
}
