package edu.mit.cci.pogs.view.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import edu.mit.cci.pogs.messages.CommunicationMessage;

@Controller
public class WorkspaceCommunicationWSController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/communication.sendMessage")
    public void getCheckin(@Payload CommunicationMessage pogsMessage) {

        Long completedTaskId = Long.parseLong(pogsMessage.getCompletedTaskId());
        //if group message send to task level topic
        //else send to channel whatever it is

        messagingTemplate.convertAndSend("/topic/public/task/"+completedTaskId + "/communication", pogsMessage);
    }
}
