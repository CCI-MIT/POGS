package edu.mit.cci.pogs.view.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import edu.mit.cci.pogs.service.WorkspaceService;

@Controller
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;


    @GetMapping("/check_in")//register
    public String register() {
        return "home";
    }

    @GetMapping("/waiting_room")
    public String waitingRoom() {
        return "home";
    }

    @GetMapping("/intro")
    public String intro() {
        return "home";
    }

    @GetMapping("/display_name")
    public String displayName() {
        return "home";
    }

    @GetMapping("/roster")
    public String roster() {
        return "home";
    }

    @GetMapping("/task/{completedTaskId}/i/{subjectExternalId}")
    public String taskIntro(@PathVariable("completedTaskId") Long completedTaskId,
                            @PathVariable("subjectExternalId") String subjectExternalId) {

        return "home";
    }
    @GetMapping("/task/{completedTaskId}/p/{subjectExternalId}")
    public String taskPrimer(@PathVariable("completedTaskId") Long completedTaskId,
                            @PathVariable("subjectExternalId") String subjectExternalId) {

        return "home";
    }
    @GetMapping("/task/{completedTaskId}/w/{subjectExternalId}")
    public String taskWork(@PathVariable("completedTaskId") Long completedTaskId,
                            @PathVariable("subjectExternalId") String subjectExternalId) {

        return "home";
    }
}
