package edu.mit.cci.pogs.view.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import edu.mit.cci.pogs.messages.CollaborationMessage;

import edu.mit.cci.pogs.messages.TodoListMessageContent;


@Controller
public class WorkspaceCollaborationWSController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/collaboration.sendMessage")
    public void getCheckin(@Payload CollaborationMessage pogsMessage) {


        TodoListMessageContent todoListMessageContent = pogsMessage.getContent();



        Long completedTaskId = Long.parseLong(pogsMessage.getCompletedTaskId());

        if(todoListMessageContent.getType().equals(CollaborationMessage.TodoType.CREATE_TODO)){
                //call service or dao for creating todoentries.

        }

        // compose the todo list broadcast message .
        CollaborationMessage allTodoEntries = new CollaborationMessage();


        messagingTemplate.convertAndSend("/topic/public/task/"+completedTaskId + "/collaboration", allTodoEntries);
    }
}
