package edu.mit.cci.pogs.view.ot;

import edu.mit.cci.pogs.ot.OperationService;
import edu.mit.cci.pogs.ot.api.Operation;
import edu.mit.cci.pogs.ot.dto.OperationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class OperationalTransformationWsController {

    private OperationService operationService;

    @Autowired
    public OperationalTransformationWsController(OperationService operationService) {
        this.operationService = operationService;
    }

    @MessageMapping("/ot/pad/{padId}/operations/submit")
    @SendTo("/topic/ot/pad/{padId}/operations")
    public OperationDto processOperation(@DestinationVariable long padId,
            @Payload OperationDto operationDto) {
        Operation operation = Operation.fromDto(operationDto);
        return operationService.processOperation(padId, operation).toDto();
    }
}
