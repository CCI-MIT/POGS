package edu.mit.cci.pogs.view.ot;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mit.cci.pogs.messages.PogsMessage;
import edu.mit.cci.pogs.messages.PogsMessage.MessageType;
import edu.mit.cci.pogs.messages.TaskAttributeMessage;
import edu.mit.cci.pogs.messages.TaskAttributeMessageContent;
import edu.mit.cci.pogs.ot.OperationService;
import edu.mit.cci.pogs.ot.api.Operation;
import edu.mit.cci.pogs.ot.dto.OperationDto;
import edu.mit.cci.pogs.utils.DateUtils;
import edu.mit.cci.pogs.view.workspace.WorkspaceTaskWSController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;

@Controller
public class OperationalTransformationWsController {

    private OperationService operationService;
    private SimpMessageSendingOperations messagingTemplate;
    private WorkspaceTaskWSController workspaceTaskWSController;

    private ObjectMapper objectMapper;

    @Autowired
    public OperationalTransformationWsController(OperationService operationService,
            SimpMessageSendingOperations messagingTemplate,
            WorkspaceTaskWSController workspaceTaskWSController, ObjectMapper objectMapper) {
        this.operationService = operationService;
        this.messagingTemplate = messagingTemplate;
        this.workspaceTaskWSController = workspaceTaskWSController;
        this.objectMapper = objectMapper;
    }

    @MessageMapping("/ot/pad/{padId}/operations/submit")
    @SendTo("/topic/ot/pad/{padId}/operations")
    public OperationDto processOperation(@DestinationVariable String padId,
            @Payload OperationDto operationDto) {
        Operation operation = Operation.fromDto(operationDto);
        return operationService.processOperation(padId, operation).toDto();
    }

    @MessageMapping("/ot.operations.submit")
//    @SendTo("/pogsapp/task.saveAttribute")
    public void processOperation(@Payload PogsMessage<String> pogsMessage)
            throws IOException {
        final OperationDto pogsMessageContent =
                objectMapper.readValue(pogsMessage.getContent(), OperationDto.class);
        Operation operation = Operation.fromDto(pogsMessageContent);
        final OperationDto operationDto = operationService.processOperation(
                operation.getPadId(), operation, true).toDto();

        final TaskAttributeMessageContent messageContent =
                new TaskAttributeMessageContent("operation", true);
        messageContent.setAttributeStringValue(objectMapper.writeValueAsString(operationDto));
        final TaskAttributeMessage message = new TaskAttributeMessage(MessageType.TASK_ATTRIBUTE,
                messageContent, pogsMessage.getSender(), null,
                pogsMessage.getCompletedTaskId(), pogsMessage.getSessionId());


//        messagingTemplate.convertAndSend("/pogsapp/task.saveAttribute", message);
//        return message;
        //TODO: workaround because the sending doesn't seem to work
        workspaceTaskWSController.getLoggableAttribute(message);
    }
}
