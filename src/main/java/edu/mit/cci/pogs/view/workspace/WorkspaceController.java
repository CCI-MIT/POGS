package edu.mit.cci.pogs.view.workspace;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.round.RoundDao;
import edu.mit.cci.pogs.model.dao.session.CommunicationConstraint;
import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.subjectattribute.SubjectAttributeDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskexecutionattribute.TaskExecutionAttributeDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
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

    @Autowired
    private TaskHasTaskConfigurationDao taskHasTaskConfigurationDao;

    @Autowired
    private TaskExecutionAttributeDao taskExecutionAttributeDao;

    @Autowired
    private SubjectAttributeDao subjectAttributeDao;

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private RoundDao roundDao;

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
                model.addAttribute("pogsSession", sr.getSession());
                model.addAttribute("secondsRemainingCurrentUrl", sr.getSession().getSecondsRemainingForCurrentUrl());
                model.addAttribute("nextUrl", sr.getSession().getNextUrl());
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
    public String displayNamePost(@PathVariable("externalId") String externalId, @RequestParam("displayName") String displayName, Model model) {
        Subject su = workspaceService.getSubject(externalId);
        if (su != null) {
            su.setSubjectDisplayName(displayName);
            subjectDao.update(su);
        }

        return checkExternalIdAndSessionRunningAndForward(su, model, "workspace/display_name");
    }

    @GetMapping("/roster/{externalId}")
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
                                                     Task task, String forward, Model model) {
        if (su == null) {
            return handleErrorMessage("There was an error and your session has ended!", model);
        }
        SessionRunner sr = SessionRunner.getSessionRunner(su.getSessionId());
        if (sr != null && !sr.getSession().getStatus().equals(SessionStatus.DONE.getStatus())) {
            if (task != null) {
                model.addAttribute("task", task);
                model.addAttribute("subject", su);
                model.addAttribute("pogsSession", sr.getSession());
                model.addAttribute("secondsRemainingCurrentUrl",
                        sr.getSession().getSecondsRemainingForCurrentUrl());
                model.addAttribute("nextUrl", sr.getSession().getNextUrl());
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

        return checkSubjectSessionTaskAndForward(su, task, "workspace/task_intro", model);


    }

    @GetMapping("/round/{roundId}/task/{taskId}/p/{subjectExternalId}")
    public String taskPrimer(@PathVariable("taskId") Long taskId,
                             @PathVariable("subjectExternalId") String subjectExternalId,
                             Model model) {

        Task task = taskDao.get(taskId);
        Subject su = workspaceService.getSubject(subjectExternalId);

        return checkSubjectSessionTaskAndForward(su, task, "workspace/task_primer", model);

    }

    private static JSONArray attributesToJsonArray(List<TaskExecutionAttribute> taskExecutionAttributes) {
        JSONArray configurationArray = new JSONArray();
        for (TaskExecutionAttribute tea : taskExecutionAttributes) {
            JSONObject teaJson = new JSONObject();
            teaJson.put("attributeName", tea.getAttributeName());
            teaJson.put("stringValue", tea.getStringValue());
            teaJson.put("doubleValue", tea.getDoubleValue());
            teaJson.put("integerValue", tea.getIntegerValue());
            configurationArray.add(teaJson);


        }
        return configurationArray;
    }

    @GetMapping("/round/{roundId}/task/{taskId}/w/{subjectExternalId}")
    public String taskWork(@PathVariable("roundId") Long roundId,
                           @PathVariable("taskId") Long taskId,
                           @PathVariable("subjectExternalId") String subjectExternalId,
                           Model model) {


        Task task = taskDao.get(taskId);
        Subject su = workspaceService.getSubject(subjectExternalId);
        Round round = roundDao.get(roundId);

        if (task != null && round != null) {
            //get task plugin type task.getTaskPluginType()
            TaskPlugin pl = TaskPlugin.getTaskPlugin(task.getTaskPluginType());
            if (pl != null) {
                //get task configurations
                TaskHasTaskConfiguration configuration = taskHasTaskConfigurationDao
                        .getByTaskIdRoundId(task.getId(), round.getId());
                List<TaskExecutionAttribute> taskExecutionAttributes = taskExecutionAttributeDao
                        .listByTaskConfigurationId(configuration.getTaskConfigurationId());



                //get task html & js from plugin file system
                model.addAttribute("taskCss", pl.getTaskCSSContent());
                model.addAttribute("taskWorkJs", pl.getTaskWorkJsContent());
                model.addAttribute("taskWorkHtml", pl.getTaskWorkHtmlContent());

                model.addAttribute("subject", su);
                model.addAttribute("task", new TaskWrapper(task));



                model.addAttribute("taskConfigurationAttributes",
                        attributesToJsonArray(taskExecutionAttributes));

                SessionRunner sr = SessionRunner.getSessionRunner(su.getSessionId());
                SessionWrapper sessionWrapper = sr.getSession();

                if (sr == null) {
                    return handleErrorMessage("There was an error and your " +
                            "session has ended!", model);
                }
                model.addAttribute("pogsSession", sessionWrapper);


                String cc = sessionWrapper.getCommunicationType();
                if(task.getCommunicationType() != null || !task.getCommunicationType().equals(cc)){
                    cc = task.getCommunicationType();
                }

                model.addAttribute("communicationType", cc);
                model.addAttribute("hasChat", (cc!=null && !cc.equals(CommunicationConstraint.NO_CHAT)?(true):(false)));


                model.addAttribute("secondsRemainingCurrentUrl",
                        sr.getSession().getSecondsRemainingForCurrentUrl());
                model.addAttribute("nextUrl", sr.getSession().getNextUrl());

                Team team = teamDao.getByRoundIdTaskId(round.getId(),task.getId());
                if (team == null) {
                    team = teamDao.getByRoundIdTaskId(round.getId(), null);
                }
                CompletedTask completedTask = completedTaskDao.getByRoundIdTaskIdTeamId(
                        round.getId(),
                        team.getId(),
                        task.getId());
                //get team
                List<Subject> teammates = subjectDao.getTeammates(su.getSessionId(),
                        round.getId(), task.getId());
                if(teammates == null || teammates.size() == 0 ){
                    teammates = subjectDao.getTeammates(su.getSessionId(),
                            round.getId(), null);
                }
                if(teammates == null || teammates.size() == 0  ){
                    teammates = subjectDao.getTeammates(su.getSessionId(),
                            null, null);
                }

                model.addAttribute("teammates", getTeamatesJSONObject(teammates));
                model.addAttribute("completedTask", completedTask);


            } else {
                return handleErrorMessage("There was an error and your session has ended!", model);
            }

        }

        return "workspace/task_work";
    }

    private JSONArray getTeamatesJSONObject(List<Subject> teammates) {
        JSONArray ja = new JSONArray();
        for(Subject s: teammates) {
            JSONObject subject = new JSONObject();
            subject.put("externalId", s.getSubjectExternalId());
            subject.put("displayName", s.getSubjectDisplayName());
            JSONArray subjectAttributes = new JSONArray();
            List<SubjectAttribute> attributes = subjectAttributeDao.listBySubjectId(s.getId());
            for(SubjectAttribute sa: attributes){
                JSONObject att = new JSONObject();
                att.put("attributeName",sa.getAttributeName());
                att.put("stringValue",sa.getStringValue());
                att.put("integerValue",sa.getIntegerValue());
                att.put("realValue",sa.getRealValue());
                subjectAttributes.add(att);
            }
            subject.put("attributes", subjectAttributes);
            ja.add(subject);
        }
        return ja;
    }

    @GetMapping("/done/{externalId}")
    public String done(@PathVariable("externalId") String externalId, Model model) {
        Subject su = workspaceService.getSubject(externalId);
        return checkExternalIdAndSessionRunningAndForward(su, model, "workspace/session_done");

    }

}