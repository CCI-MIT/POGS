package edu.mit.cci.pogs.view.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.mit.cci.pogs.service.SessionLogService;
import edu.mit.cci.pogs.view.workspace.beans.ErrorLogBean;

@RestController
public class WorkspaceErrorReporter {

    @Autowired
    public SessionLogService sessionLogService;


    @GetMapping("/log/{sessionId}")
    public ErrorLogBean landingPageLogin(@PathVariable("sessionId") Long sessionId,
                                         @RequestParam(name = "externalId", required = false) String externalId,
                                         @RequestParam(name = "errorMessage", required = false) String errorMessage,
                                         @RequestParam(name = "url", required = false) String url,
                                         Model model,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {

        sessionLogService.createLogFromClient(externalId,errorMessage,sessionId, url);

        return new ErrorLogBean("ok");
    }
}
