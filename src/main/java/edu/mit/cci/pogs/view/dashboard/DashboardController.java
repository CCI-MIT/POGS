package edu.mit.cci.pogs.view.dashboard;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.cci.pogs.config.AuthUserDetailsService;
import edu.mit.cci.pogs.model.dao.session.TaskExecutionType;
import edu.mit.cci.pogs.model.dao.sessionlog.SessionLogDao;
import edu.mit.cci.pogs.model.dao.study.StudyDao;

import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasSessionCheckIn;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.SessionRunnerManager;
import edu.mit.cci.pogs.runner.wrappers.RoundWrapper;
import edu.mit.cci.pogs.runner.wrappers.SessionSchedule;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.runner.wrappers.TeamWrapper;
import edu.mit.cci.pogs.service.SessionLogService;
import edu.mit.cci.pogs.service.StudyService;
import edu.mit.cci.pogs.service.SubjectHasSessionCheckInService;

@Controller
public class DashboardController {

    @Autowired
    private StudyDao studyDao;

    @Autowired
    private StudyService studyService;

    @Autowired
    private SessionLogDao sessionLogDao;

    @Autowired
    private SubjectHasSessionCheckInService subjectHasSessionCheckInService;

        @GetMapping("/admin/dashboard/sessionrunner")
    public String sessionRunner(Model model) {
        Collection<SessionRunner> liveSessionRunners = SessionRunnerManager.getLiveRunners();
        Collection<SessionRunner> livePerpetuals = new ArrayList<>();
        Collection<SessionRunner> liveScheduleds = new ArrayList<>();
        for (SessionRunner sr : liveSessionRunners) {
            sr.getCheckedInWaitingSubjectListById();
            if(sr.getSession().isSessionPerpetual()){
                livePerpetuals.add(sr);
            } else {
                liveScheduleds.add(sr);

            }
        }
        model.addAttribute("liveSessions",liveSessionRunners);
        model.addAttribute("livePerpetuals",livePerpetuals);
        model.addAttribute("liveScheduleds",liveScheduleds);
        return "dashboard/dashboard-advanced";
    }



    @GetMapping("/admin/dashboard/sessionlog/{sessionId}")
    public String sessionRunnerLog(@PathVariable("sessionId") Long sessionId,Model model) {

        model.addAttribute("logs",sessionLogDao.listTodayLogs(sessionId));
        return "dashboard/dashboard-advanced-logs";
    }

    @GetMapping("/admin/dashboard/subjectcheckin/{sessionId}")
    public String sessionSubjectCheckIn(@PathVariable("sessionId") Long sessionId,Model model) {

        model.addAttribute("subjectsToJoin",subjectHasSessionCheckInService.listReadyToJoinSubjects(sessionId));

        model.addAttribute("subjectsCheckedIn",subjectHasSessionCheckInService.listCheckedInSubjects(sessionId));
        model.addAttribute("subjectsLost",subjectHasSessionCheckInService.listLostSubjects(sessionId));
        return "dashboard/dashboard-advanced-subjects";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        //get session associated with user
        List<Study> studiesUserIsAllowedToSee = studyDao.listStudiesWithUserGroup(
                AuthUserDetailsService.getLoggedInUser());

        Collection<SessionRunner> liveSessionRunners = SessionRunnerManager.getLiveRunners();
        List<Session> liveSessionsResearcherCanSee = new ArrayList<>();

        for (SessionRunner sr : liveSessionRunners) {
            for (Study study : studiesUserIsAllowedToSee) {
                if (study.getId() == sr.getSession().getStudyId()) {
                    liveSessionsResearcherCanSee.add(sr.getSession());
                }
            }
        }

        model.addAttribute("liveSessionsResearcherCanSee", studyService.groupSessionsByBaseSession(liveSessionsResearcherCanSee));
        return "dashboard/dashboard-home";
    }

    @GetMapping("/admin/dashboard/sessions/{sessionId}")
    public String dashboardForSession(@PathVariable("sessionId") Long sessionId, Model model) {
        //get all schedules and completed tasks for session
        SessionRunner sessionRunner = SessionRunnerManager.getSessionRunner(sessionId);

        List<RoundWrapper> rw = sessionRunner.getSession().getSessionRounds();

        if (sessionRunner.getSession().isSessionPerpetual()) {
            model.addAttribute("sessionz", sessionRunner.getSession());
            return dashboardForPerpetual(model, sessionId);
        }
        if (rw != null && rw.size() > 0) {

            List<TeamWrapper> teams = rw.get(0).getRoundTeams();
            model.addAttribute("teams", teams);
        }

        List<SessionSchedule> sessionSchedule = sessionRunner.getSession().getSessionSchedule();
        if (sessionRunner.getSession().getTaskExecutionType().equals(TaskExecutionType.PARALLEL_FIXED_ORDER.getId().toString())) {
            List<TaskWrapper> taskWrappers = sessionRunner.getSession().getTaskList();

            for (int i = 0; i < sessionSchedule.size(); i++) {
                SessionSchedule ss = sessionSchedule.get(i);
                if (ss.getTaskReference() != null) {

                    for (TaskWrapper tw : taskWrappers) {
                        if (tw.getId() != ss.getTaskReference().getId()) {
                            SessionSchedule schedule = new SessionSchedule(ss.getStartTimestamp(),
                                    ss.getEndTimestamp(),
                                    tw,
                                    ss.getRoundReference(),
                                    ss.getSessionReference(),
                                    tw.getTaskWorkUrl());
                            sessionSchedule.add(i, schedule);
                        }
                    }
                    break;
                }
            }
        }

        model.addAttribute("sessionz", sessionRunner.getSession());
        model.addAttribute("sessionSchedule", sessionSchedule);
        model.addAttribute("completedTasksByTeam", getCompletedTasksForTeamsByTask(
                sessionRunner.getSession()).toString());

        return "dashboard/dashboard-session";
    }

    private String dashboardForPerpetual(Model model, Long sessionId) {

        List<SubjectHasSessionCheckInBean> readyToJoinSubjects = subjectHasSessionCheckInService.listReadyToJoinSubjectsBean(sessionId);
        List<SubjectHasSessionCheckInBean> lostSubjects = subjectHasSessionCheckInService.listLostSubjects(sessionId);

        List<SubjectHasSessionCheckInBean> listCheckedInSubjects = subjectHasSessionCheckInService.listCheckedInSubjects(sessionId);
        //group by session
        // add session_list and subjects.

        Map<Long, List<SubjectHasSessionCheckInBean>> sessionUser = new HashMap<>();
        for (SubjectHasSessionCheckInBean shscib : listCheckedInSubjects) {
            List<SubjectHasSessionCheckInBean> list = sessionUser.get(shscib.getJoinedSessionId());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(shscib);
            sessionUser.put(shscib.getJoinedSessionId(), list);
        }
        List<SessionHasSubjects> sessionHasSubjectsList = new ArrayList<>();
        for(Long sessId: sessionUser.keySet()){
            SessionRunner sr = SessionRunnerManager.getSessionRunner(sessId);
            if(sr!=null){
                SessionHasSubjects shs = new SessionHasSubjects();
                shs.setSession(sr.getSession());
                shs.setSubjects(sessionUser.get(sessId));
                sessionHasSubjectsList.add(shs);
            }
        }

        model.addAttribute("readyToJoinSubjects", readyToJoinSubjects);
        model.addAttribute("lostSubjects", lostSubjects);
        model.addAttribute("sessionHasSubjectsList", sessionHasSubjectsList);

        return "dashboard/dashboard-perpetual";
    }

    private JSONObject getCompletedTasksForTeamsByTask(SessionWrapper sessionWrapper) {

        JSONObject jo = new JSONObject();
        for (TaskWrapper tw : sessionWrapper.getTaskList()) {
            JSONArray ja = new JSONArray();

            for (CompletedTask ct : tw.getCompletedTasks()) {
                JSONObject ctJson = new JSONObject();
                ctJson.put("teamId", ct.getTeamId());
                ctJson.put("subjectId", ct.getSubjectId());
                ctJson.put("completedTaskId", ct.getId());
                ja.add(ctJson);
            }
            jo.put(tw.getId(), ja);
        }
        return jo;
    }
}
