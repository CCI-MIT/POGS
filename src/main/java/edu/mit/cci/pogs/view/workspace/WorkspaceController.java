package edu.mit.cci.pogs.view.workspace;

import org.jooq.tools.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.dao.round.RoundDao;
import edu.mit.cci.pogs.model.dao.session.CommunicationConstraint;
import edu.mit.cci.pogs.model.dao.session.ScoreboardDisplayType;
import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.dao.session.TaskExecutionType;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.subjectattribute.SubjectAttributeDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.SessionRunnerManager;
import edu.mit.cci.pogs.runner.wrappers.RoundWrapper;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.runner.wrappers.TeamWrapper;
import edu.mit.cci.pogs.service.CompletedTaskAttributeService;
import edu.mit.cci.pogs.service.EventLogService;
import edu.mit.cci.pogs.service.SessionService;
import edu.mit.cci.pogs.service.SubjectService;
import edu.mit.cci.pogs.service.TaskExecutionAttributeService;
import edu.mit.cci.pogs.service.TaskScoreService;
import edu.mit.cci.pogs.service.TaskService;
import edu.mit.cci.pogs.service.TeamService;
import edu.mit.cci.pogs.service.WorkspaceService;
import edu.mit.cci.pogs.utils.ColorUtils;
import edu.mit.cci.pogs.utils.DateUtils;


@Controller
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    @Autowired
    private TaskScoreService taskScoreService;

    @Autowired
    private EventLogService eventLogService;

    @Autowired
    private TaskExecutionAttributeService taskExecutionAttributeService;

    @Autowired
    private CompletedTaskAttributeService completedTaskAttributeService;

    @Autowired
    private SubjectAttributeDao subjectAttributeDao;

    @Autowired
    private TeamService teamService;

    @Autowired
    private RoundDao roundDao;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ExecutableScriptDao executableScriptDao;

    @GetMapping("/sessions/{sessionId}")
    public String landingPageLogin(@PathVariable("sessionId") String sessionId,
                                   @RequestParam(name = "externalId", required = false) String externalId,
                                   Model model) {
        model.addAttribute("action", "/sessions/start/" + sessionId);
        model.addAttribute("externalId", externalId);
        return "/workspace/landing";
    }

    @GetMapping("/sessions/start/{sessionId}")
    public String landingPageLoginPost(@PathVariable("sessionId") String sessionId,
                                       @RequestParam(name = "externalId", required = false) String externalId,
                                       Model model) {

        Session session = sessionService.getSessionByFullName(sessionId);
        if (session == null) {
            model.addAttribute("errorMessage", "This session url was not recognized.");
            return "workspace/error";
        }

        Long now = DateUtils.now();
        if (session.getPerpetualStartDate().getTime() > now ||
                session.getPerpetualEndDate().getTime() < now) {
            model.addAttribute("errorMessage", "Too late, this session expired.");
            return "workspace/error";
        }


        Subject su = new Subject();
        List<SubjectAttribute> allSubAttr = null;
        if (externalId != null) {
            Subject ref = subjectDao.getByExternalId(externalId);
            if (ref != null) {
                su.setPreviousSessionSubject(ref.getId());
                allSubAttr = subjectAttributeDao.listBySubjectId(ref.getId());
            }


        }
        String newSubjectExtId = session.getFullSessionName() + "_" + now;
        su.setSubjectExternalId(newSubjectExtId);
        su.setSubjectDisplayName(newSubjectExtId);
        su.setSessionId(session.getId());

        su = subjectDao.create(su);

        if (allSubAttr != null) {
            for (SubjectAttribute sa : allSubAttr) {
                if(!(sa.getAttributeName().equals(
                        ColorUtils.SUBJECT_DEFAULT_BACKGROUND_COLOR_ATTRIBUTE_NAME)
                        || sa.getAttributeName().equals(
                                ColorUtils.SUBJECT_DEFAULT_FONT_COLOR_ATTRIBUTE_NAME))) {
                    SubjectAttribute subjectAttribute = new SubjectAttribute();
                    subjectAttribute.setSubjectId(su.getId());
                    subjectAttribute.setAttributeName(sa.getAttributeName());
                    subjectAttribute.setStringValue(sa.getStringValue());
                    subjectAttribute.setIntegerValue(sa.getIntegerValue());
                    subjectAttribute.setRealValue(sa.getRealValue());
                    subjectAttribute.setLatest(true);
                    subjectAttributeDao.create(subjectAttribute);
                }
            }
        }

        return "redirect:/check_in/?externalId=" + su.getSubjectExternalId();
    }


    @RequestMapping(value = "/check_in", method = {RequestMethod.GET, RequestMethod.POST})
    public String register(@RequestParam("externalId") String externalId, Model model) {

        Subject su;
        //check if subject is from perpetual session.
        Session session = sessionService.getPerpetualSessionForSubject(externalId);
        if (session != null) {
            su = new Subject();
            su.setSubjectExternalId(externalId);
            su.setSessionId(session.getId());
        } else {

            su = workspaceService.getSubject(externalId);
            if (su == null) {
                model.addAttribute("errorMessage", "This id was not recognized.");
                return "workspace/error";
            }
        }
        SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());
        if (sr == null) {
            model.addAttribute("errorMessage", "Too early.");
            return "workspace/error";
        }
        if (sr.getSession().getStatus().equals(SessionStatus.DONE.getStatus())) {
            model.addAttribute("errorMessage", "Your session has ended!: " + su.getId() + " - " + su.getSessionId());
            return "workspace/error";
        }
        if (sr.getSession().isTooLate()) {
            model.addAttribute("errorMessage", "You are too late, your session has already passed!");
            return "workspace/error";
        }

        sr.subjectCheckIn(su);
        return "redirect:/waiting_room/" + su.getSubjectExternalId();
    }

    @GetMapping("/waiting_room/{externalId}")
    public String waitingRoom(@PathVariable("externalId") String externalId, Model model) {
        Subject su = workspaceService.getSubject(externalId);
        if (su == null) {
            Session session = sessionService.getPerpetualSessionForSubject(externalId);

            if (session != null) {
                su = new Subject();
                su.setSubjectExternalId(externalId);
                su.setSessionId(session.getId());
            } else {
                model.addAttribute("errorMessage", "There was an error and your session has ended!");
                return "workspace/error";
            }
        }


        return checkExternalIdAndSessionRunningAndForward(su, model, "workspace/waiting_room");
    }

    private String checkExternalIdAndSessionRunningAndForward(Subject su, Model model, String forwardString) {

        if (su != null) {

            SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());
            if (sr != null && !sr.getSession().getStatus().equals(SessionStatus.DONE.getStatus())) {
                model.addAttribute("subject", su);
                model.addAttribute("pogsSession", sr.getSession());
                if(sr.getSession().getSessionWideScriptId()!=null){
                    ExecutableScript es = executableScriptDao.get(sr.getSession().getSessionWideScriptId());
                    if(es!=null) {
                        model.addAttribute("sessionWideScript", es.getScriptContent());
                    }
                }
                model.addAttribute("pogsSessionPerpetual", sr.getSession().isSessionPerpetual());

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

            List<Subject> teammates = teamService.getTeamSubjects(su.getId(), su.getSessionId(), null, null);
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
        SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());
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

        TaskPlugin pl = TaskPlugin.getTaskPlugin(task.getTaskPluginType());

        model.addAttribute("taskCss", pl.getTaskCSSContent());
        model.addAttribute("taskPrimerJs", pl.getTaskPrimerJsContent());
        model.addAttribute("taskPrimerHtml", pl.getTaskPrimerHtmlContent());


        model.addAttribute("taskConfigurationAttributes",
                        taskExecutionAttributeService.listExecutionAttributesAsJsonArray(
                                task.getId()));
        return checkSubjectSessionTaskAndForward(su, task, "workspace/task_primer", model);

    }


    @GetMapping("/task/{taskId}/t/{subjectExternalId}")
    public String taskConfigTest(@PathVariable("taskId") Long taskId,
                                 @PathVariable("subjectExternalId") String subjectExternalId,
                                 Model model) {

        Task task = taskDao.get(taskId);
        TaskPlugin pl = TaskPlugin.getTaskPlugin(task.getTaskPluginType());
        if (pl != null) {

            model.addAttribute("task", new TaskWrapper(task));
            model.addAttribute("taskConfigurationAttributes",
                    taskExecutionAttributeService.listExecutionAttributesAsJsonArray(task.getId()));

            JSONArray allLogs = new JSONArray();
            model.addAttribute("eventsUntilNow", allLogs);

            //get task html & js from plugin file system
            model.addAttribute("taskCss", pl.getTaskCSSContent());
            model.addAttribute("taskWorkJs", pl.getTaskWorkJsContent());
            model.addAttribute("taskWorkHtml", pl.getTaskWorkHtmlContent());

            model.addAttribute("subject", teamService.generateFakeSubject(subjectExternalId));
            model.addAttribute("teammates", teamService.getFakeTeamatesJSONObject());


            model.addAttribute("subjectCanTalkTo", teamService.getFakeSubjectCanTalkTo());

            model.addAttribute("hasCollaborationTodoListEnabled",
                    task.getCollaborationTodoListEnabled());
            model.addAttribute("hasCollaborationFeedbackWidget",
                    task.getCollaborationFeedbackWidgetEnabled());
            model.addAttribute("hasCollaborationVotingWidget",
                    task.getCollaborationVotingWidgetEnabled());

            model.addAttribute("allTasksList", taskService.getFakeJsonTaskList());
            model.addAttribute("lastTask", "");

            String cc = task.getCommunicationType();

            model.addAttribute("communicationType", cc);
            model.addAttribute("hasChat", (cc != null && !cc.equals(CommunicationConstraint
                    .NO_CHAT.getId().toString()) ? (true) : (false)));

            //TODO: FIGURE OUT A WAY TO RUN THE CODE BEFORE.
            //if (pl.getTaskBeforeWorkJsContent() != null) {
            //    runTaskBeforeWorkScript(pl, teammates, taskConfigurationAttributes, configuration.getTaskConfigurationId());
            //}

        }

        return "workspace/task_workpreview";
    }

    @GetMapping("/taskplugin/{taskPlugin}/{pluginConfig}/w/{subjectExternalId}")
    public String taskWorkPluginTest(
            @PathVariable("taskPlugin") String taskPlugin,
            @PathVariable("pluginConfig") String pluginConfig,
            @PathVariable("subjectExternalId") String subjectExternalId,
            Model model) {

        TaskPlugin pl = TaskPlugin.getTaskPlugin(taskPlugin);
        if (pl != null) {

            model.addAttribute("taskConfigurationAttributes",
                    taskExecutionAttributeService.listExecutionAttributesFromPluginConfigAsJsonArray(pluginConfig));
            //get task html & js from plugin file system
            model.addAttribute("taskCss", pl.getTaskCSSContent());
            model.addAttribute("taskWorkJs", pl.getTaskWorkJsContent());
            model.addAttribute("taskWorkHtml", pl.getTaskWorkHtmlContent());


            model.addAttribute("eventsUntilNow", new JSONArray());
            model.addAttribute("subject", teamService.generateFakeSubject(subjectExternalId));

            model.addAttribute("teammates", teamService.getFakeTeamatesJSONObject());

            model.addAttribute("allTasksList", taskService.getFakeJsonTaskList());

            model.addAttribute("lastTask", "");

        }

        return "workspace/task_workplugin";
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
            TaskPlugin pl = TaskPlugin.getTaskPlugin(task.getTaskPluginType());
            if (pl != null) {

                //get task html & js from plugin file system
                model.addAttribute("taskCss", pl.getTaskCSSContent());
                model.addAttribute("taskWorkJs", pl.getTaskWorkJsContent());
                model.addAttribute("taskWorkHtml", pl.getTaskWorkHtmlContent());

                model.addAttribute("subject", su);
                model.addAttribute("subjectCanTalkTo", subjectService.getSubjectsSubjectIsAllowedToTalkJson(su.getId()));
                model.addAttribute("channelSubjectIsIn", subjectService.getChannelsSubjectIsIn(su.getId()));


                model.addAttribute("task", new TaskWrapper(task));
                model.addAttribute("round", new RoundWrapper(round));


                SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());
                SessionWrapper sessionWrapper = sr.getSession();

                if (sr == null) {
                    return handleErrorMessage("There was an error and your " +
                            "session has ended!", model);
                }
                model.addAttribute("pogsSession", sessionWrapper);

                if (sessionWrapper.isTaskExecutionModeParallel()) {
                    model.addAttribute("hasTabs", true);
                    model.addAttribute("taskList", sessionWrapper.getTaskList());
                    //add all tasks
                } else {
                    model.addAttribute("hasTabs", false);
                }

                if (sessionWrapper.getTaskExecutionType().equals(TaskExecutionType.PARALLEL_FIXED_ORDER.getId().toString())) {
                    model.addAttribute("lastTask", "");
                } else {
                    TaskWrapper lastTask = null;
                    for (int i = 0; i < sessionWrapper.getTaskList().size(); i++) {
                        TaskWrapper tw = sessionWrapper.getTaskList().get(i);

                        if (tw.getId() == task.getId()) {
                            if (i == 0) {
                                model.addAttribute("lastTask", "");
                            } else {
                                model.addAttribute("lastTask", sessionWrapper.getTaskList().get(i - 1).getTaskName());
                            }
                        }
                    }
                }
                model.addAttribute("allTasksList", taskService.getJsonTaskList(sessionWrapper.getTaskList()));
                String cc = sessionWrapper.getCommunicationType();
                if (task.getCommunicationType() != null || !task.getCommunicationType().equals(cc)) {
                    cc = task.getCommunicationType();
                }

                model.addAttribute("communicationType", cc);
                model.addAttribute("chatBotName", sessionWrapper.getChatBotName());
                model.addAttribute("hasChat", (cc != null && !cc.equals(CommunicationConstraint
                        .NO_CHAT.getId().toString()) ? (true) : (false)));

                boolean hasCollaborationTodoListEnabled = sessionWrapper
                        .getCollaborationTodoListEnabled();
                if (task.getCollaborationTodoListEnabled() != null) {
                    hasCollaborationTodoListEnabled = task.getCollaborationTodoListEnabled();
                }
                model.addAttribute("hasCollaborationTodoListEnabled", hasCollaborationTodoListEnabled);

                boolean hasCollaborationFeedbackWidget = sessionWrapper
                        .getCollaborationFeedbackWidgetEnabled();


                if (task.getCollaborationFeedbackWidgetEnabled() != null) {
                    hasCollaborationFeedbackWidget = task.getCollaborationFeedbackWidgetEnabled();
                }
                model.addAttribute("hasCollaborationFeedbackWidget", hasCollaborationFeedbackWidget);

                boolean hasCollaborationVotingWidget = sessionWrapper
                        .getCollaborationVotingWidgetEnabled();

                if (task.getCollaborationVotingWidgetEnabled() != null) {
                    hasCollaborationVotingWidget = task.getCollaborationVotingWidgetEnabled();
                }

                model.addAttribute("hasCollaborationVotingWidget", hasCollaborationVotingWidget);

                model.addAttribute("secondsRemainingCurrentUrl",
                        sr.getSession().getSecondsRemainingForCurrentUrl());
                model.addAttribute("nextUrl", sr.getSession().getNextUrl());

                Team team = teamService.getTeamCascadeConfig(su.getId(), sessionWrapper.getId(), round.getId(), taskId);

                if (team == null) {
                    return handleErrorMessage(sessionWrapper.getCouldNotAssignToTeamMessage(), model);
                }
                CompletedTask completedTask = completedTaskDao.getByRoundIdTaskIdTeamId(
                        round.getId(),
                        team.getId(),
                        task.getId());
                if (completedTask == null) {
                    completedTask = completedTaskDao.getBySubjectIdTaskId(su.getId(), taskId);
                }


                if(sr.getSession().getSessionWideScriptId()!=null){
                    ExecutableScript es = executableScriptDao.get(sr.getSession().getSessionWideScriptId());
                    if(es!=null) {
                        model.addAttribute("sessionWideScript", es.getScriptContent());
                    }
                }

                model.addAttribute("completedTask", completedTask);

                model.addAttribute("eventsUntilNow", eventLogService.getAllLogsUntilNow(completedTask.getId()));

                model.addAttribute("teammates", teamService.getTeamatesJSONObject(teamService.getTeamMates(task, su, round)));

                model.addAttribute("taskConfigurationAttributes",
                        taskExecutionAttributeService.listExecutionAttributesAsJsonArray(task.getId()));

                model.addAttribute("completedTaskAttributes",
                        completedTaskAttributeService.listCompletedTaskAttributesForCompletedTask(completedTask.getId()));


            } else {
                return handleErrorMessage("There was an error and your session has ended!", model);
            }

        }

        return "workspace/task_work";
    }

    @GetMapping("/done/{externalId}")
    public String done(@PathVariable("externalId") String externalId, Model model) {
        Subject su = workspaceService.getSubject(externalId);
        SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());

        if (sr != null) {

            if (sr.getSession().getScoreboardDisplayType().equals(ScoreboardDisplayType.
                    DISPLAY_SESSION.getId().toString())) {

                List<TeamWrapper> teamWrappers = sr.getSession()
                        .getSessionRounds().get(0).getRoundTeams();


                int subjectsTeam = 0;
                for (int i = 0; i < teamWrappers.size(); i++) {
                    for (Subject sub : teamWrappers.get(i).getSubjects()) {
                        if (sub.getId() == su.getId()) {
                            subjectsTeam = i;
                        }
                    }
                }
                model.addAttribute("subjectsTeamIndex", subjectsTeam);
                model.addAttribute("showSubjectName", sr.getSession().getScoreboardUseDisplayNames());
                model.addAttribute("showScore", true);
                model.addAttribute("taskScoreWrappers", taskScoreService.getTaskScoreWrappers(
                        sr.getSession().getTaskList(),teamWrappers, su.getId() ));
                model.addAttribute("teamWrappers", teamWrappers);

            } else {
                model.addAttribute("showScore", false);
            }
        }
        return checkExternalIdAndSessionRunningAndForward(su, model,
                "workspace/session_done");

    }

}