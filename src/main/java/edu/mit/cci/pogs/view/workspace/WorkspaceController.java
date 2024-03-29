package edu.mit.cci.pogs.view.workspace;

import com.blueconic.browscap.BrowsCapField;
import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;

import org.jooq.tools.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import edu.mit.cci.pogs.model.jooq.tables.pojos.IndividualSubjectScore;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasSessionCheckIn;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.runner.PreviewTaskBeforeWorkRunner;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.SessionRunnerManager;
import edu.mit.cci.pogs.runner.wrappers.RoundWrapper;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.runner.wrappers.TeamWrapper;
import edu.mit.cci.pogs.service.CompletedTaskAttributeService;
import edu.mit.cci.pogs.service.DictionaryService;
import edu.mit.cci.pogs.service.EventLogService;
import edu.mit.cci.pogs.service.IndividualSubjectScoreService;
import edu.mit.cci.pogs.service.SessionService;
import edu.mit.cci.pogs.service.SubjectHasSessionCheckInService;
import edu.mit.cci.pogs.service.SubjectService;
import edu.mit.cci.pogs.service.TaskExecutionAttributeService;
import edu.mit.cci.pogs.service.TaskScoreService;
import edu.mit.cci.pogs.service.TaskService;
import edu.mit.cci.pogs.service.TeamService;
import edu.mit.cci.pogs.service.WorkspaceService;
import edu.mit.cci.pogs.utils.DateUtils;


@Controller
public class WorkspaceController {

    @Autowired
    private Environment env;

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
    private IndividualSubjectScoreService individualSubjectScoreService;

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

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private SubjectHasSessionCheckInService subjectHasSessionCheckInService;


    private static final Logger _log = LoggerFactory.getLogger(WorkspaceController.class);

    @GetMapping("/sessions/{sessionId}")
    public String landingPageLogin(@PathVariable("sessionId") String sessionId,
                                   @RequestParam(name = "externalId", required = false) String externalId,

                                   @RequestParam(name = "workerId", required = false) String workerId,
                                   @RequestParam(name = "assignmentId", required = false) String assignmentId,
                                   @RequestParam(name = "hitId", required = false) String hitId,
                                   @RequestParam(name = "PROLIFIC_PID", required = false) String PROLIFIC_PID,
                                   @RequestParam(name = "STUDY_ID", required = false) String STUDY_ID,
                                   @RequestParam(name = "SESSION_ID", required = false) String SESSION_ID,
                                   Model model,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        Session session = sessionService.getSessionByFullName(sessionId);

        if (!isCurrentPerpetualSessionValid(model, session)) {
            return "workspace/error";
        }

        String userAgent = request.getHeader("User-Agent");
        String referrer = request.getHeader("referer");

        boolean isPogsRef = false;
        //if (referrer != null) {
            //isPogsRef = (referrer.contains("https://pogs.info") || referrer.contains("http://localhost"));
        //}
        model.addAttribute("isPogsReferrer", isPogsRef);

        if (userAgent.contains("Firefox") || userAgent.contains("MSIE")) {

            model.addAttribute("browser", (userAgent.contains("MSIE") ? ("Internet Explorer") : ("Firefox")));
            return "workspace/unsupported";
        }

        model.addAttribute("action", "/sessions/start/" + sessionId);

        if(PROLIFIC_PID != null){
            model.addAttribute("PROLIFIC_PID", PROLIFIC_PID);
        }
        if(STUDY_ID!=null){
            model.addAttribute("STUDY_ID", STUDY_ID);
        }
        if(SESSION_ID!=null){
            model.addAttribute("SESSION_ID",SESSION_ID);
        }

        if (workerId != null) {
            model.addAttribute("workerId", workerId);
        }
        if (assignmentId != null) {
            model.addAttribute("assignmentId", assignmentId);
        }
        if (hitId != null) {
            model.addAttribute("hitId", hitId);
        }
        model.addAttribute("externalId", externalId);

        boolean alreadyHasCookie = false;
        String cookieName = "unique_pogs_id_" + session.getId();
        if (request.getCookies() != null && request.getCookies().length > 0) {
            for (Cookie co : request.getCookies()) {
                if (co.getName().equals(cookieName)) {
                    alreadyHasCookie = true;
                    break;
                }
            }
        }
        if (!alreadyHasCookie) {
            Cookie co = new Cookie(cookieName, UUID.randomUUID().toString());
            co.setMaxAge(session.getWaitingRoomTime());
            response.addCookie(co);
        }

        if(session.getLandingPageOverrideEnabled()){
            model.addAttribute("pogsSession",session);
            return "workspace/customized_landing";
        } else {
            return "workspace/landing";
        }
    }

    @GetMapping("/sessions/start/{sessionId}")
    public String landingPageLoginPost(@PathVariable("sessionId") String sessionId,
                                       @RequestParam(name = "externalId", required = false) String externalId,


                                       HttpServletRequest request,
                                       Model model) {

        Session session = sessionService.getSessionByFullName(sessionId);

        if (!isCurrentPerpetualSessionValid(model, session)) {
            return "workspace/error";
        }


        boolean alreadyHasCookie = false;
        String cookieHash = null;
        String cookieName = "unique_pogs_id_" + session.getId();
        if (request.getCookies() != null && request.getCookies().length > 0) {
            for (Cookie co : request.getCookies()) {
                if (co.getName().equals(cookieName)) {
                    alreadyHasCookie = true;
                    cookieHash = co.getValue();
                }
            }
        }

        Subject su;
        if (alreadyHasCookie) {
            su = subjectDao.getByPogsUniqueHashId(cookieHash);
            if (su != null) {
                SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());

                model.addAttribute("pogsSessionPerpetual", sr.getSession().isSessionPerpetual());
                return checkExternalIdAndSessionRunningAndForward(su, model, "workspace/pre_check_in");
            }
        }

        su = new Subject();
        List<SubjectAttribute> allSubAttr = null;
        if (externalId != null && !externalId.isEmpty()) {
            Subject ref = subjectDao.getByExternalId(externalId);
            if (ref != null) {
                su.setPreviousSessionSubject(ref.getId());
                su.setSubjectDisplayName(ref.getSubjectDisplayName());
                allSubAttr = subjectAttributeDao.listBySubjectId(ref.getId());
            }
        }

        String newSubjectExtId = session.getFullSessionName() + "_" + UUID.randomUUID().toString();
        su.setSubjectExternalId(newSubjectExtId);
        su.setSubjectDisplayName(newSubjectExtId);
        su.setSessionId(session.getId());

        if (cookieHash != null) {
            su.setPogsUniqueHash(cookieHash);
        }
        su = subjectService.createSubjectSafeExternalId(su);

        createSubjectAttributeFromUserAgent(request, su);

        createSubjectAttributesFromParameters(su, request);

        retroactivelyCopySubjectAttributes(su, allSubAttr);
        //Check in user in session runner for perpetual session.
        SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());
        if (sr != null) {
            sr.subjectCheckIn(su);
        } else {
            model.addAttribute("errorMessage", "Too late, this session expired.");
            return "workspace/error";
        }
        //go to pre-check-in page (wait for event CHECKIN OPEN)
        //return "redirect:/check_in/?externalId=" + su.getSubjectExternalId();
        model.addAttribute("pogsSessionPerpetual", sr.getSession().isSessionPerpetual());


        return checkExternalIdAndSessionRunningAndForward(su, model, "workspace/pre_check_in");
    }

    private void createSubjectAttributeFromUserAgent(HttpServletRequest request, Subject su) {
        String userAgent = request.getHeader("User-Agent");

        SubjectAttribute saa = new SubjectAttribute();
        saa.setSubjectId(su.getId());
        saa.setAttributeName("userAgent");
        saa.setStringValue(userAgent);
        saa.setInternalAttribute(true);
        saa.setLatest(true);
        subjectAttributeDao.create(saa);
    }

    private void retroactivelyCopySubjectAttributes(Subject su, List<SubjectAttribute> allSubAttr) {
        String[] parametersAllowed = {"workerId", "hitId", "assignmentId", "PROLIFIC_PID", "STUDY_ID", "SESSION_ID"};

        if (allSubAttr != null) {
            for (SubjectAttribute sa : allSubAttr) {
                Boolean shouldCreate = false;

                if(!(sa.getInternalAttribute())){
                    shouldCreate = true;
                }
                if(!shouldCreate){
                    for (String param : parametersAllowed) {
                        if ((sa.getAttributeName().equals(param))){
                            shouldCreate = true;
                        }
                    }
                }
                if(shouldCreate) {
                    SubjectAttribute subjectAttribute = new SubjectAttribute();
                    subjectAttribute.setSubjectId(su.getId());
                    subjectAttribute.setAttributeName(sa.getAttributeName());
                    subjectAttribute.setStringValue(sa.getStringValue());
                    subjectAttribute.setIntegerValue(sa.getIntegerValue());
                    subjectAttribute.setRealValue(sa.getRealValue());
                    subjectAttribute.setInternalAttribute(sa.getInternalAttribute());
                    subjectAttribute.setLatest(true);
                    subjectAttributeDao.create(subjectAttribute);
                }
            }
        }
    }

    private void createSubjectAttributesFromParameters(Subject su, HttpServletRequest request) {

        Enumeration<String> parameternames = request.getParameterNames();

        List<SubjectAttribute> allSubAttr2 = new ArrayList<>();
        while (parameternames.hasMoreElements()) {
            String parameterName = parameternames.nextElement();
            SubjectAttribute sa = new SubjectAttribute();
            sa.setInternalAttribute(true);
            sa.setAttributeName(parameterName);
            sa.setStringValue(request.getParameter(parameterName));
            sa.setLatest(true);
            allSubAttr2.add(sa);
        }

        for (SubjectAttribute sa : allSubAttr2) {
            SubjectAttribute subjectAttribute = new SubjectAttribute();
            subjectAttribute.setSubjectId(su.getId());
            subjectAttribute.setAttributeName(sa.getAttributeName());
            subjectAttribute.setStringValue(sa.getStringValue());
            subjectAttribute.setIntegerValue(sa.getIntegerValue());
            subjectAttribute.setRealValue(sa.getRealValue());
            subjectAttribute.setInternalAttribute(sa.getInternalAttribute());
            subjectAttribute.setLatest(true);
            subjectAttributeDao.create(subjectAttribute);
        }

    }


    private boolean isCurrentPerpetualSessionValid(Model model, Session session) {
        if (session == null) {
            model.addAttribute("errorMessage", "This session url was not recognized.");
            return false;
        }


        Long now = DateUtils.now();
        if (session.getPerpetualStartDate().getTime() > now ||
                session.getPerpetualEndDate().getTime() < now) {
            model.addAttribute("errorMessage", "Too late, this session expired.");
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/expired", method = {RequestMethod.GET, RequestMethod.POST})
    public String expired(@RequestParam("externalId") String externalId, Model model,HttpServletRequest request,HttpServletResponse response) {
        Subject su;
        su = workspaceService.getSubject(externalId);

        eraseCookies(request,response);
        return checkExternalIdAndSessionRunningAndForward(su, model, "workspace/expired");
    }

    @RequestMapping(value = "/check_in", method = {RequestMethod.GET, RequestMethod.POST})
    public String register(@RequestParam("externalId") String externalId, Model model) {

        Subject su;

        su = workspaceService.getSubject(externalId);
        if (su == null) {
            model.addAttribute("errorMessage", "This id was not recognized.");
            return "workspace/error";
        }

        SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());
        if (sr == null) {
            model.addAttribute("errorMessage", "Session id: " + su.getSessionId() + "  Too early.");
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
        System.out.println("Right before check in");
        sr.subjectCheckIn(su);
        System.out.println("Right after check in");
        if(sr.getSession().getCommunicationType().equals(CommunicationConstraint.NO_CHAT.getId().toString())) {
            return "redirect:/waiting_room/" + su.getSubjectExternalId();
        } else {
            return "redirect:/session_global_chat/" + su.getSubjectExternalId();
        }
    }

    @GetMapping("/waiting_room/{externalId}")
    public String waitingRoom(@PathVariable("externalId") String externalId, Model model) {
        return forwardToWaitingRoomOrGlobalChatRoom(externalId, model, "workspace/waiting_room");
    }

    @GetMapping("/session_global_chat/{externalId}")
    public String sessionGlobalChat(@PathVariable("externalId") String externalId, Model model) {
        //add stuff to make pogs.js work



        Subject su = workspaceService.getSubject(externalId);
        SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());

        //Task task = taskDao.get(taskId);
        Round round = (sr.getSession().getSessionRounds().get(0));

        model.addAttribute("subject", su);
        model.addAttribute("subjectCanTalkTo", subjectService.getSubjectsSubjectIsAllowedToTalkJson(su.getId()));
        model.addAttribute("channelSubjectIsIn", subjectService.getChannelsSubjectIsIn(su.getId()));


        //model.addAttribute("task", new TaskWrapper(task));
        //model.addAttribute("round", new RoundWrapper(round));

        SessionWrapper sessionWrapper = sr.getSession();
        model.addAttribute("pogsSession", sessionWrapper);
        if (sessionWrapper.isTaskExecutionModeParallel()) {
            model.addAttribute("hasTabs", true);
            model.addAttribute("taskList", sessionWrapper.getTaskList());
            //add all tasks
        } else {
            model.addAttribute("hasTabs", false);
        }
        String cc = sr.getSession().getCommunicationType();
        model.addAttribute("communicationType", cc);
        model.addAttribute("chatBotName", sessionWrapper.getChatBotName());
        model.addAttribute("hasChat", (cc != null && !cc.equals(CommunicationConstraint
            .NO_CHAT.getId().toString()) ? (true) : (false)));
        model.addAttribute("hasCollaborationTodoListEnabled", false);
        model.addAttribute("hasCollaborationFeedbackWidget", false);
        model.addAttribute("hasCollaborationVotingWidget", false);
        model.addAttribute("secondsRemainingCurrentUrl",
            sr.getSession().getSecondsRemainingForCurrentUrl());
        model.addAttribute("nextUrl", sr.getSession().getNextUrl());

        if(sessionWrapper.getGlobalChatCompletedTaskId()== null){

        }
        CompletedTask completedTask = completedTaskDao.get(sessionWrapper.getGlobalChatCompletedTaskId());
        model.addAttribute("completedTask", completedTask);

        model.addAttribute("eventsUntilNow", eventLogService.getAllLogsUntilNow(completedTask.getId()));

        model.addAttribute("teammates", teamService.getTeamatesJSONObject(teamService.getTeamMates(sessionWrapper.getTaskList().get(0), su, round)));

        return forwardToWaitingRoomOrGlobalChatRoom(externalId, model, "workspace/session_global_chat");
    }

    public String forwardToWaitingRoomOrGlobalChatRoom(@PathVariable("externalId") String externalId, Model model,String forwadString) {
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


        return checkExternalIdAndSessionRunningAndForward(su, model, forwadString);
    }

    private String checkExternalIdAndSessionRunningAndForward(Subject su, Model model, String forwardString) {

        if (su != null) {

            SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());
            if(sr.getSession().getCommunicationType().equals(CommunicationConstraint.NO_CHAT.getId().toString())){
                model.addAttribute("template", "layouts/workspace-layout.html");
            } else {
                model.addAttribute("template", "layouts/workspace-iframe-layout.html");
                model.addAttribute("videoProviderAppId", env.getProperty("videoprovider.app_id"));
            }


            if (sr != null && !sr.getSession().getStatus().equals(SessionStatus.DONE.getStatus())) {
                SessionWrapper session = sr.getSession();
                model.addAttribute("subject", su);
                model.addAttribute("pogsSession", session);
                if (sr.getSession().getSessionWideScriptId() != null) {
                    ExecutableScript es = executableScriptDao.get(session.getSessionWideScriptId());
                    if (es != null) {
                        model.addAttribute("sessionWideScript", es.getScriptContent());
                    }
                }
                model.addAttribute("pogsSessionPerpetual", sr.getSession().isSessionPerpetual());

                model.addAttribute("secondsRemainingCurrentUrl", sr.getSession().getSecondsRemainingForCurrentUrl());
                String url = sr.getSession().getNextUrl();

                if (url.contains("qualtrix")) {
                    //add parameters
                    String workerId = "";
                    String assignmentId = "";
                    String hitId = "";
                    List<SubjectAttribute> subjectAttributes = subjectAttributeDao.listBySubjectId(su.getId());


                    for (SubjectAttribute sa : subjectAttributes) {
                        if (sa.getAttributeName().equals("workerId")) {
                            workerId = sa.getStringValue();
                        }
                        if (sa.getAttributeName().equals("assignmentId")) {
                            assignmentId = sa.getStringValue();
                        }
                        if (sa.getAttributeName().equals("hitId")) {
                            hitId = sa.getStringValue();
                        }
                    }
                    if (url.contains("?")) {
                        url = url + "&workerId=" + workerId + "&assignmentId=" + assignmentId + "&hitId=" + hitId + "&pogsExternalId=" + su.getSubjectExternalId();
                    } else {
                        url = url + "?workerId=" + workerId + "&assignmentId=" + assignmentId + "&hitId=" + hitId + "&pogsExternalId=" + su.getSubjectExternalId();
                    }
                }
                model.addAttribute("nextUrl", url);


                if (session.isSessionPerpetual() && su.getId() != null) {
                    model.addAttribute("waitingRoomExpireTime", subjectHasSessionCheckInService.getSubjectSessionCheckInExpireTime(su.getId(), session.getId()));
                }
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

                if(sr.getSession().getCommunicationType().equals(CommunicationConstraint.NO_CHAT.getId()+ "")){
                    model.addAttribute("template", "layouts/workspace-layout.html");
                } else {
                    model.addAttribute("videoProviderAppId", env.getProperty("videoprovider.app_id"));
                    model.addAttribute("template", "layouts/workspace-iframe-layout.html");
                }

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


        org.json.JSONArray executionAttributes =
                taskExecutionAttributeService.listExecutionAttributesAsJsonArray(task.getId());
        model.addAttribute("taskConfigurationAttributes",
                executionAttributes);

        return checkSubjectSessionTaskAndForward(su, task, "workspace/task_primer", model);

    }

    @GetMapping("/task/{taskId}/p/{subjectExternalId}")
    public String taskPrimerPreview(@PathVariable("taskId") Long taskId,
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

        model.addAttribute("task", task);
        Subject fakeSub = teamService.generateFakeSubject(subjectExternalId);
        model.addAttribute("subject", fakeSub);
        //model.addAttribute("pogsSession", sr.getSession());
        model.addAttribute("secondsRemainingCurrentUrl",
                DateUtils.toMilliseconds(task.getPrimerTime()));

        return "workspace/task_primerpreview";

    }

    @GetMapping("/task/{taskId}/i/{subjectExternalId}")
    public String taskIntroPreview(
            @PathVariable("taskId") Long taskId,
            @PathVariable("subjectExternalId") String subjectExternalId,
            Model model) {

        Task task = taskDao.get(taskId);
        Subject su = workspaceService.getSubject(subjectExternalId);

        model.addAttribute("task", task);
        Subject fakeSub = teamService.generateFakeSubject(subjectExternalId);
        model.addAttribute("subject", fakeSub);
        //model.addAttribute("pogsSession", sr.getSession());
        model.addAttribute("secondsRemainingCurrentUrl",
                DateUtils.toMilliseconds(task.getIntroTime()));


        return "workspace/task_intropreview";


    }

    @GetMapping("/task/{taskId}/t/{subjectExternalId}")
    public String taskConfigTest(@PathVariable("taskId") Long taskId,
                                 @PathVariable("subjectExternalId") String subjectExternalId,
                                 Model model) {

        Task task = taskDao.get(taskId);
        TaskPlugin pl = TaskPlugin.getTaskPlugin(task.getTaskPluginType());
        TaskWrapper tw = new TaskWrapper(task);
        if (pl != null) {

            model.addAttribute("task", tw);
            model.addAttribute("secondsRemainingCurrentUrl",
                    +DateUtils.toMilliseconds(task.getInteractionTime()));
            org.json.JSONArray executionAttributes =
                    taskExecutionAttributeService.listExecutionAttributesAsJsonArray(task.getId());
            model.addAttribute("taskConfigurationAttributes",
                    executionAttributes);

            model.addAttribute("dictionary", dictionaryService.getDictionaryJSONObjectForTask(task.getId()));

            JSONArray allLogs = new JSONArray();
            model.addAttribute("eventsUntilNow", allLogs);

            //get task html & js from plugin file system
            model.addAttribute("taskCss", pl.getTaskCSSContent());
            model.addAttribute("taskWorkJs", pl.getTaskWorkJsContent());
            model.addAttribute("taskWorkHtml", pl.getTaskWorkHtmlContent());

            Subject fakeSub = teamService.generateFakeSubject(subjectExternalId);
            model.addAttribute("subject", fakeSub);
            org.json.JSONArray team = teamService.getFakeTeamatesJSONArray();


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


            if (pl.getTaskBeforeWorkJsContent() != null) {
                startBeforeWorkScript(null, tw, pl, executionAttributes, fakeSub, team, model);

            }
            if (!model.containsAttribute("completedTaskAttributes")) {
                model.addAttribute("completedTaskAttributes", "[]");
            }

            if (!model.containsAttribute("teammates")) {
                model.addAttribute("teammates", team);
            }


        }

        return "workspace/task_workpreview";
    }

    private void startBeforeWorkScript(SessionWrapper session, TaskWrapper task, TaskPlugin pl,
                                       org.json.JSONArray executionAttributes, Subject fakeSubject,
                                       org.json.JSONArray team,
                                       Model model) {
        PreviewTaskBeforeWorkRunner csr = (PreviewTaskBeforeWorkRunner) context.getBean("previewTaskBeforeWorkRunner");

        //csr.setSession(session);
        task.setTaskStartTimestamp(new Date().getTime());
        CompletedTask fakeCT = new CompletedTask();
        fakeCT.setId(-1l);
        fakeCT.setSolo(task.getSoloTask().toString());

        if (task.getSoloTask()) {
            fakeCT.setSubjectId(fakeSubject.getId());
        } else {

        }
        csr.setCompletedTask(fakeCT);

        task.getCompletedTasks().add(fakeCT);
        csr.setTaskWrapper(task);
        csr.setTaskPlugin(pl);
        csr.setExecutionAttributes(executionAttributes);
        csr.setFakeSubject(fakeSubject);
        csr.setTeam(team);
        csr.run();

        if (csr.getCompletedTaskAttributesToAdd() != null && !csr.getCompletedTaskAttributesToAdd().isEmpty()) {
            model.addAttribute("completedTaskAttributes", csr.getCompletedTaskAttributesToAdd());
        } else {
            model.addAttribute("completedTaskAttributes", "[{" +
                    "    \"attributeName\": \"padID\"," +
                    "    \"stringValue\": \"TESTPAD\"" +
                    "}]");
        }

        if (csr.getSubjectAttributesToAdd() != null) {
            org.json.JSONArray jo = new org.json.JSONArray(csr.getSubjectAttributesToAdd());
            if (jo.length() > 0) {
                org.json.JSONArray team2 = new org.json.JSONArray();
                Map<String, JSONObject> users = new HashMap<>();
                for (int j = 0; j < team.length(); j++) {
                    JSONObject su = team.getJSONObject(j);
                    users.put(su.getString("externalId"), su);
                }
                for (int i = 0; i < jo.length(); i++) {

                    JSONObject attributeToAdd = jo.getJSONObject(i);
                    for (int j = 0; j < team.length(); j++) {
                        JSONObject subject = team.getJSONObject(j);
                        if (subject.getString("externalId").equals(attributeToAdd.getString("externalId"))) {

                            org.json.JSONArray attributesToAdd = attributeToAdd.getJSONArray("attributes");
                            org.json.JSONArray currentAttributes = subject.getJSONArray("attributes");
                            for (int k = 0; k < attributesToAdd.length(); k++) {
                                currentAttributes.put(attributesToAdd.getJSONObject(k));
                            }
                            subject.put("attributes", currentAttributes);
                            users.put(subject.getString("externalId"), subject);
                        }
                    }
                }
                for (String s : users.keySet()) {
                    team2.put(users.get(s));
                }
                model.addAttribute("teammates", team2);
            }
        }
    }

    @GetMapping("/taskplugin/{taskPlugin}/{pluginConfig}/w/{subjectExternalId}")
    public String taskWorkPluginTest(
            @PathVariable("taskPlugin") String taskPlugin,
            @PathVariable("pluginConfig") long pluginConfigId,
            @PathVariable("subjectExternalId") String subjectExternalId,
            Model model) {

        TaskPlugin pl = TaskPlugin.getTaskPlugin(taskPlugin);
        if (pl != null) {

            model.addAttribute("taskConfigurationAttributes",
                    taskExecutionAttributeService.listExecutionAttributesFromPluginConfigAsJsonArray(pluginConfigId));

            model.addAttribute("dictionary", dictionaryService.getDictionaryJSONObjectForTaskPlugin(pluginConfigId));


            //get task html & js from plugin file system
            model.addAttribute("taskCss", pl.getTaskCSSContent());
            model.addAttribute("taskWorkJs", pl.getTaskWorkJsContent());
            model.addAttribute("taskWorkHtml", pl.getTaskWorkHtmlContent());


            model.addAttribute("eventsUntilNow", new JSONArray());
            model.addAttribute("subject", teamService.generateFakeSubject(subjectExternalId));

            model.addAttribute("teammates", teamService.getFakeTeamatesJSONArray());

            model.addAttribute("allTasksList", taskService.getFakeJsonTaskList());

            model.addAttribute("lastTask", "");

        }

        return "workspace/task_workplugin";
    }

    private void eraseCookies(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }
    }

    @GetMapping("/round/{roundId}/task/{taskId}/w/{subjectExternalId}")
    public String taskWork(@PathVariable("roundId") Long roundId,
                           @PathVariable("taskId") Long taskId,
                           @PathVariable("subjectExternalId") String subjectExternalId,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           Model model) {


        this.eraseCookies(request, response);

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

                if (sr == null) {
                    _log.info("Session with id: " + su.getSessionId() + "(" + round.getSessionId() + ")" + "URL:" + request.getRequestURI());
                    return handleErrorMessage("There was an error and your " +
                            "session has ended!", model);
                }

                SessionWrapper sessionWrapper = sr.getSession();
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
                model.addAttribute("videoProviderAppId", env.getProperty("videoprovider.app_id"));
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


                if (sr.getSession().getSessionWideScriptId() != null) {
                    ExecutableScript es = executableScriptDao.get(sr.getSession().getSessionWideScriptId());
                    if (es != null) {
                        model.addAttribute("sessionWideScript", es.getScriptContent());
                    }
                }

                model.addAttribute("completedTask", completedTask);

                model.addAttribute("eventsUntilNow", eventLogService.getAllLogsUntilNow(completedTask.getId()));

                model.addAttribute("teammates", teamService.getTeamatesJSONObject(teamService.getTeamMates(task, su, round)));


                model.addAttribute("dictionary", dictionaryService.getDictionaryJSONObjectForTask(task.getId()));

                model.addAttribute("taskConfigurationAttributes",
                        taskExecutionAttributeService.listExecutionAttributesAsJsonArray(task.getId()));

                model.addAttribute("completedTaskAttributes",
                        completedTaskAttributeService.listCompletedTaskAttributesForCompletedTask(completedTask.getId()));

                if(sr.getSession().getCommunicationType().equals(CommunicationConstraint.NO_CHAT.getId().toString())){
                    model.addAttribute("template", "layouts/workspace-layout.html");
                } else {
                    model.addAttribute("videoProviderAppId", env.getProperty("videoprovider.app_id"));
                    model.addAttribute("template", "layouts/workspace-iframe-layout.html");
                }
            } else {
                return handleErrorMessage("There was an error and your session has ended!", model);
            }

        }

        return "workspace/task_work";
    }

    @GetMapping("/round/{roundId}/task/{taskId}/s/{subjectExternalId}")
    public String taskScoreWork(@PathVariable("roundId") Long roundId,
                           @PathVariable("taskId") Long taskId,
                           @PathVariable("subjectExternalId") String subjectExternalId,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           Model model) {

        Task task = taskDao.get(taskId);
        Subject su = workspaceService.getSubject(subjectExternalId);
        Round round = roundDao.get(roundId);
        SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());
        Team team = teamService.getTeamCascadeConfig(su.getId(), sr.getSession().getId(), round.getId(), taskId);
        CompletedTask completedTask = completedTaskDao.getByRoundIdTaskIdTeamId(
                round.getId(),
                team.getId(),
                task.getId());
        if (completedTask == null) {
            completedTask = completedTaskDao.getBySubjectIdTaskId(su.getId(), taskId);
        }

        List<Subject> teammates = teamService.getTeamMates(task,su,round);

        SessionWrapper sessionWrapper = sr.getSession();
        model.addAttribute("pogsSession", sessionWrapper);
        model.addAttribute("teammates", teammates);

        model.addAttribute("subject", su);
        model.addAttribute("completedTask", completedTask);

        model.addAttribute("showSubjectName", sr.getSession().getScoreboardUseDisplayNames());
        model.addAttribute("showScore", true);
        model.addAttribute("secondsRemainingCurrentUrl", sr.getSession().getSecondsRemainingForCurrentUrl());


        if(sr.getSession().getCommunicationType().equals(CommunicationConstraint.NO_CHAT.getId().toString())){
            model.addAttribute("template", "layouts/workspace-layout.html");
        } else {
            model.addAttribute("template", "layouts/workspace-iframe-layout.html");
        }

        return "workspace/task_scoring";
    }

    @GetMapping("/scoring/{externalId}")
    public String scoring(@PathVariable("externalId") String externalId, Model model) {
        Subject su = workspaceService.getSubject(externalId);
        SessionRunner sr = SessionRunnerManager.getSessionRunner(su.getSessionId());

        if (sr != null) {
            model.addAttribute("showScore", false);
        }


        model.addAttribute("teammates", teamService.getTeamatesJSONObject(teamService.getTeamMates(
                sr.getSession().getTaskList().get(0), su,
                sr.getSession().getSessionRounds().get(0))));
        return checkExternalIdAndSessionRunningAndForward(su, model,
                "workspace/session_done");
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


                model.addAttribute("teammates", teamService.getTeamatesJSONObject(teamService.getTeamMates(
                        sr.getSession().getTaskList().get(0), su,
                        sr.getSession().getSessionRounds().get(0))));
                model.addAttribute("subjectsTeamIndex", subjectsTeam);
                model.addAttribute("showSubjectName", sr.getSession().getScoreboardUseDisplayNames());
                model.addAttribute("showScore", true);
                model.addAttribute("taskScoreWrappers", taskScoreService.getTaskScoreWrappers(sr.getSession().getScoreboardAverageSoloSession(),
                        sr.getSession().getTaskList(), teamWrappers, su.getId()));
                model.addAttribute("teamWrappers", teamWrappers);

            } else {
                model.addAttribute("showScore", false);
            }
        }
        return checkExternalIdAndSessionRunningAndForward(su, model,
                "workspace/session_done");

    }

}