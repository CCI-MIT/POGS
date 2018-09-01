package edu.mit.cci.pogs.view.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.mit.cci.pogs.config.AuthUserDetailsService;
import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;

@Controller
public class DashboardController {

    @Autowired
    private StudyDao studyDao;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        //get session associated with user
        List<Study> studiesUserIsAllowedToSee = studyDao.listStudiesWithUserGroup(
                AuthUserDetailsService.getLoggedInUser());

        Collection<SessionRunner> liveSessionRunners = SessionRunner.getLiveRunners();
        List<SessionWrapper> liveSessionsResearcherCanSee = new ArrayList<>();

        for (SessionRunner sr : liveSessionRunners) {
        }

        model.addAttribute("","");
        return "dashboard";
    }

    @GetMapping("/dashboard/session/{sessionId}")
    public String dashboardForSession(@PathVariable("sessionId") String sessionId, Model model) {
        //get all schedules and completed tasks for session

        // dashboard.js
        return null;
    }
}
