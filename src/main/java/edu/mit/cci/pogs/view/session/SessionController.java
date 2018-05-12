package edu.mit.cci.pogs.view.session;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

import edu.mit.cci.pogs.model.dao.session.CommunicationConstraint;
import edu.mit.cci.pogs.model.dao.session.ScoreboardDisplayType;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.dao.session.TaskExecutionType;
import edu.mit.cci.pogs.model.dao.session.TeamCreationMethod;
import edu.mit.cci.pogs.model.dao.session.TeamCreationTime;
import edu.mit.cci.pogs.model.dao.session.TeamCreationType;
import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Condition;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;

import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.view.session.beans.SessionBean;

@Controller
public class SessionController {
 
    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private StudyDao studyDao;

    private static final String DEFAULT_CONDITION_NAME = "";




    @ModelAttribute("teamCreationMethods")
    public List<TeamCreationMethod> getTeamCreationMethods() {
        return Arrays.asList(TeamCreationMethod.values());
    }

    @ModelAttribute("teamCreationTypes")
    public List<TeamCreationType> getTeamCreationTypes() {
        return Arrays.asList(TeamCreationType.values());
    }

    @ModelAttribute("teamCreationTimes")
    public List<TeamCreationTime> getTeamCreationTimes() {
        return Arrays.asList(TeamCreationTime.values());
    }

    @ModelAttribute("scoreboardDisplayTypes")
    public List<ScoreboardDisplayType> getScoreboardDisplayTypes() {
        return Arrays.asList(ScoreboardDisplayType.values());
    }

    @ModelAttribute("communicationConstraints")
    public List<CommunicationConstraint> getCommunicationConstraints() {
        return Arrays.asList(CommunicationConstraint.values());
    }
    @ModelAttribute("taskExecutionTypes")
    public List<TaskExecutionType> getTaskExecutionTypes() {
        return Arrays.asList(TaskExecutionType.values());
    }

    @ModelAttribute("sessionStatuses")
    public List<SessionStatus> getSessionStatuses() {
        return Arrays.asList(SessionStatus.values());
    }
 
    @GetMapping("/sessions/")
    public String getSession(Model model) {
 
        model.addAttribute("sessionsList", sessionDao.list());
        return "session/session-list";
    }
 
    @GetMapping("{id}")
    public String getSessions(@PathVariable("id") Long id, Model model) {
 
        model.addAttribute("session", sessionDao.get(id));
        return "session/session-display";
    }
 
    @GetMapping("/admin/studies/{studyId}/sessions/create")
    public String createSession(@PathVariable("studyId") Long studyId, Model model) {
        SessionBean session = new SessionBean();
        Study study = studyDao.get(studyId);

        Condition condition = new Condition();
        condition.setConditionName(DEFAULT_CONDITION_NAME);
        condition.setStudyId(studyId);
        model.addAttribute("study", study);
        model.addAttribute("session", session);
        return "session/session-edit";
    }
 
    @GetMapping("/admin/study/{studyId}/sessions/{id}/edit")
    public String editSession(@PathVariable("studyId") Long studyId,@PathVariable("id") Long id, Model model) {
        model.addAttribute("session", sessionDao.get(id));
        return "session/session-edit";
    }
 
    @PostMapping("/sessions/")
    public String saveSession(@ModelAttribute SessionBean session, RedirectAttributes redirectAttributes) {
 
        if (session.getId() == null) {
            //sessionDao.create(session);
            MessageUtils.addSuccessMessage("Session created successfully!",redirectAttributes);
        }else{
            //sessionDao.update(session);
            MessageUtils.addSuccessMessage("Session updated successfully!",redirectAttributes);
        }
 
        return "redirect:/admin/sessions";
    }
 
}
