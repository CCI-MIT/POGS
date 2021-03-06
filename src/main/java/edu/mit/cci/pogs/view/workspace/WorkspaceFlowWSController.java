package edu.mit.cci.pogs.view.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import edu.mit.cci.pogs.messages.PogsMessage;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.SessionRunnerManager;
import edu.mit.cci.pogs.service.WorkspaceService;

@Controller
public class WorkspaceFlowWSController {

    @Autowired
    private WorkspaceService workspaceService;

    @MessageMapping("/flow.checkIn")
    public void getCheckin(@Payload PogsMessage pogsMessage) {
        if(pogsMessage.getType().equals(PogsMessage.MessageType.CHECK_IN)) {
            String externalId = pogsMessage.getSender();
            Subject subject = workspaceService.getSubject(externalId);

            if (subject != null) {
                SessionRunner sr = SessionRunnerManager.getSessionRunner(subject.getSessionId());
                if (sr != null) {
                    sr.subjectCheckIn(subject);
                }

            }
        }
    }

}
