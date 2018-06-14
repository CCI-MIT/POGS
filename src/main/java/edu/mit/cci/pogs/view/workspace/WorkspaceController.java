package edu.mit.cci.pogs.view.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;

import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.dao.teamhassubject.TeamHasSubjectDao;

import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.service.WorkspaceService;

@Controller
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    @PostMapping("/check_in")
    public String register(@RequestParam("externalId") String externalId, Model model) {

        Subject su = workspaceService.getSubject(externalId);
        if (su == null) {
            model.addAttribute("errorMessage", "This id was not recognized.");
            return "workspace/error";
        }
        SessionRunner sr = SessionRunner.getSessionRunner(su.getSessionId());
        if (sr == null) {
            model.addAttribute("errorMessage", "Too early.");
            return "workspace/error";
        }
        if (sr.getSession().getStatus().equals(SessionStatus.DONE.getStatus())) {
            model.addAttribute("errorMessage", "Your session has ended!");
            return "workspace/error";
        }
        sr.subjectCheckIn(su);
        return "redirect:/waiting_room/" + su.getSubjectExternalId();
    }

    @GetMapping("/waiting_room/{externalId}")
    public String waitingRoom(@PathVariable("externalId") String externalId, Model model) {
        Subject su = workspaceService.getSubject(externalId);
        return checkExternalIdAndSessionRunningAndForward(su, model, "workspace/waiting_room");
    }

    private String checkExternalIdAndSessionRunningAndForward(Subject su, Model model, String forwardString) {

        if (su != null) {

            SessionRunner sr = SessionRunner.getSessionRunner(su.getSessionId());
            if (sr != null && !sr.getSession().getStatus().equals(SessionStatus.DONE.getStatus())) {
                model.addAttribute("subject", su);
                model.addAttribute("session", su.getSessionId());
                model.addAttribute("secondsRemainingCurrentUrl", sr.getSession().getSecondsRemainingForCurrentUrl());
                return forwardString;
            } else {
                return handleErrorMessage("Your session has ended!", model);
            }
        }

        return handleErrorMessage("This id was not recognized!", model);

    }

    @GetMapping("/intro/{externalId}")
    public String intro(@PathVariable("externalId") String externalId, Model model) {
        Subject su = workspaceService.getSubject(externalId);
        //handle no TEAM FOR subject
        return checkExternalIdAndSessionRunningAndForward(su, model, "workspace/session_intro");

    }

    @GetMapping("/display_name/{externalId}")
    public String displayName(@PathVariable("externalId") String externalId, Model model) {
        Subject su = workspaceService.getSubject(externalId);
        return checkExternalIdAndSessionRunningAndForward(su, model, "workspace/display_name");


    }

    @PostMapping("/display_name/{externalId}")
    public String displayNamePost(@PathVariable("externalId") String externalId, @Param("displayName") String displayName, Model model) {
        Subject su = workspaceService.getSubject(externalId);
        if (su != null) {
            su.setSubjectDisplayName(displayName);
            subjectDao.update(su);
        }

        return checkExternalIdAndSessionRunningAndForward(su, model, "workspace/display_name");
    }

    @GetMapping("/roster")
    public String roster(@PathVariable("externalId") String externalId, Model model) {
        Subject su = workspaceService.getSubject(externalId);
        String ret = checkExternalIdAndSessionRunningAndForward(su, model, "workspace/team_roster");

        if (su != null) {
            //discover subject team
            List<Subject> teammates = subjectDao.getTeammates(su.getSessionId(), null, null);
            model.addAttribute("teammates", teammates);
        }

        return ret;
    }

    private String handleErrorMessage(String message, Model model) {
        model.addAttribute("errorMessage", message);
        return "workspace/error";
    }

    private String checkSubjectSessionTaskAndForward(Subject su,
                                                     Task task, String forward, Model model){
        if (su == null) {
            return handleErrorMessage("There was an error and your session has ended!", model);
        }
        SessionRunner sr = SessionRunner.getSessionRunner(su.getSessionId());
        if (sr != null && !sr.getSession().getStatus().equals(SessionStatus.DONE.getStatus())) {
            if (task != null) {
                model.addAttribute("task", task);
                model.addAttribute("subject", su);
                model.addAttribute("session", su.getSessionId());
                model.addAttribute("secondsRemainingCurrentUrl",
                        sr.getSession().getSecondsRemainingForCurrentUrl());
                return forward;
            } else {
                return handleErrorMessage("There was an error and your session has ended!", model);
            }
        } else {
            return handleErrorMessage("There was an error and your session has ended!", model);
        }
    }
    @GetMapping("/round/{roundId}/task/{taskId}/i/{subjectExternalId}")
    public String taskIntro(@PathVariable("roundId") Long roundId,
                            @PathVariable("taskId") Long taskId,
                            @PathVariable("subjectExternalId") String subjectExternalId,
                            Model model) {

        Task task = taskDao.get(taskId);
        Subject su = workspaceService.getSubject(subjectExternalId);

        return checkSubjectSessionTaskAndForward(su, task,"workspace/task_intro", model);


    }

    @GetMapping("/round/{roundId}/task/{taskId}/p/{subjectExternalId}")
    public String taskPrimer(@PathVariable("taskId") Long taskId,
                             @PathVariable("subjectExternalId") String subjectExternalId,
                             Model model) {

        Task task = taskDao.get(taskId);
        Subject su = workspaceService.getSubject(subjectExternalId);

        return checkSubjectSessionTaskAndForward(su, task,"workspace/task_primer", model);

    }

    @GetMapping("/round/{roundId}/task/{taskId}/w/{subjectExternalId}")
    public String taskWork(@PathVariable("taskId") Long taskId,
                           @PathVariable("subjectExternalId") String subjectExternalId) {



        Task task = taskDao.get(taskId);
        Subject su = workspaceService.getSubject(subjectExternalId);
        CompletedTask ct;

        if (task != null) {
            //get task plugin type task.getTaskPluginType()

            //get task configurations
            //get task html & js from plugin file system
            //get team
        }

        return "workspace/task_work";
    }
}