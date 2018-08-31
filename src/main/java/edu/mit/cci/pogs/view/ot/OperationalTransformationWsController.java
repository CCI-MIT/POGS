package edu.mit.cci.pogs.view.ot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mit.cci.pogs.messages.PogsMessage;
import edu.mit.cci.pogs.messages.PogsMessage.MessageType;
import edu.mit.cci.pogs.messages.TaskAttributeMessage;
import edu.mit.cci.pogs.messages.TaskAttributeMessageContent;
import edu.mit.cci.pogs.ot.OperationService;
import edu.mit.cci.pogs.ot.api.Operation;
import edu.mit.cci.pogs.ot.dto.OperationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class OperationalTransformationWsController {

    private OperationService operationService;
    private SimpMessageSendingOperations messagingTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public OperationalTransformationWsController(OperationService operationService,
            SimpMessageSendingOperations messagingTemplate) {
        this.operationService = operationService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/ot/pad/{padId}/operations/submit")
    @SendTo("/topic/ot/pad/{padId}/operations")
    public OperationDto processOperation(@DestinationVariable String padId,
            @Payload OperationDto operationDto) {
        Operation operation = Operation.fromDto(operationDto);
        return operationService.processOperation(padId, operation).toDto();
    }

    @MessageMapping("/ot.operations.submit")
    public void processOperation(@Payload PogsMessage<OperationDto> pogsMessage)
            throws JsonProcessingException {
        Operation operation = Operation.fromDto(pogsMessage.getContent());
        final OperationDto operationDto =
                operationService.processOperation(pogsMessage.getCompletedTaskId(), operation)
                                .toDto();

        final TaskAttributeMessageContent messageContent =
                new TaskAttributeMessageContent("operation", true);
        messageContent.setAttributeStringValue(objectMapper.writeValueAsString(operationDto));
        final TaskAttributeMessage message = new TaskAttributeMessage(MessageType.TASK_ATTRIBUTE,
                messageContent, pogsMessage.getSender(), null,
                pogsMessage.getCompletedTaskId(), pogsMessage.getSessionId());

        messagingTemplate.convertAndSend("/pogsapp/task.saveAttribute", message);
    }
}
