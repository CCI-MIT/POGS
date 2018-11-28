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
import java.util.List;

import edu.mit.cci.pogs.config.AuthUserDetailsService;
import edu.mit.cci.pogs.model.dao.session.TaskExecutionType;
import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.SessionRunnerManager;
import edu.mit.cci.pogs.runner.wrappers.RoundWrapper;
import edu.mit.cci.pogs.runner.wrappers.SessionSchedule;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.runner.wrappers.TeamWrapper;

@Controller
public class DashboardController {

    @Autowired
    private StudyDao studyDao;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        //get session associated with user
        List<Study> studiesUserIsAllowedToSee = studyDao.listStudiesWithUserGroup(
                AuthUserDetailsService.getLoggedInUser());

        Collection<SessionRunner> liveSessionRunners = SessionRunnerManager.getLiveRunners();
        List<SessionWrapper> liveSessionsResearcherCanSee = new ArrayList<>();

        for (SessionRunner sr : liveSessionRunners) {
            for(Study study: studiesUserIsAllowedToSee) {
                if(study.getId() == sr.getSession().getStudyId()){
                    liveSessionsResearcherCanSee.add(sr.getSession());
                }
            }
        }

        model.addAttribute("liveSessionsResearcherCanSee",liveSessionsResearcherCanSee);
        return "dashboard/dashboard-home";
    }

    @GetMapping("/admin/dashboard/sessions/{sessionId}")
    public String dashboardForSession(@PathVariable("sessionId") Long sessionId, Model model) {
        //get all schedules and completed tasks for session
        SessionRunner sessionRunner = SessionRunnerManager.getSessionRunner(sessionId);

        List<RoundWrapper> rw = sessionRunner.getSession().getSessionRounds();
        if(rw!=null && rw.size() > 0) {

            List<TeamWrapper> teams = rw.get(0).getRoundTeams();
            model.addAttribute("teams", teams);
        }

        List<SessionSchedule> sessionSchedule = sessionRunner.getSession().getSessionSchedule();
        if(sessionRunner.getSession().getTaskExecutionType().equals(TaskExecutionType.PARALLEL_FIXED_ORDER.getId().toString())) {
            List<TaskWrapper> taskWrappers = sessionRunner.getSession().getTaskList();

            for (int i =0; i < sessionSchedule.size(); i++){
                SessionSchedule ss = sessionSchedule.get(i);
                if(ss.getTaskReference()!=null){

                    for(TaskWrapper tw: taskWrappers){
                        if(tw.getId() != ss.getTaskReference().getId()){
                            SessionSchedule schedule = new SessionSchedule(ss.getStartTimestamp(),
                                    ss.getEndTimestamp(),
                                    tw,
                                    ss.getRoundReference(),
                                    ss.getSessionReference(),
                                    tw.getTaskWorkUrl());
                            sessionSchedule.add(i,schedule);
                        }
                    }
                    break;
                }
            }
        }

        model.addAttribute("sessionz",sessionRunner.getSession());
        model.addAttribute("sessionSchedule", sessionSchedule);
        model.addAttribute("completedTasksByTeam",getCompletedTasksForTeamsByTask(
                sessionRunner.getSession()).toString());

        return "dashboard/dashboard-session";
    }

    private JSONObject getCompletedTasksForTeamsByTask(SessionWrapper sessionWrapper){

        JSONObject jo = new JSONObject();
        for(TaskWrapper tw: sessionWrapper.getTaskList()){
            JSONArray ja = new JSONArray();

            for(CompletedTask ct: tw.getCompletedTasks()){
                JSONObject ctJson = new JSONObject();
                ctJson.put("teamId", ct.getTeamId());
                ctJson.put("subjectId", ct.getSubjectId());
                ctJson.put("completedTaskId", ct.getId());
                ja.add(ctJson);
            }
            jo.put(tw.getId(),ja);
        }
        return jo;
    }
}
