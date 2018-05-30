package edu.mit.cci.pogs.view.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
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
import edu.mit.cci.pogs.model.dao.taskgroup.TaskGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Condition;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroup;
import edu.mit.cci.pogs.service.SessionService;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.utils.SqlTimestampPropertyEditor;
import edu.mit.cci.pogs.view.session.beans.SessionBean;
import edu.mit.cci.pogs.view.session.beans.SessionHasTaskGroupRelationshipBean;
import edu.mit.cci.pogs.view.session.beans.SubjectsBean;

@Controller
public class SessionController {

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private StudyDao studyDao;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private TaskGroupDao taskGroupDao;

    private static final String DEFAULT_CONDITION_NAME = "";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Timestamp.class, new SqlTimestampPropertyEditor());
    }

    @ModelAttribute("taskGroups")
    public List<TaskGroup> getTaskGroups() {

        return taskGroupDao.list();
    }

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

    @GetMapping("/admin/studies/{studyId}/sessions/{id}")
    public String getSessions(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id, Model model) {
        Study study = studyDao.get(studyId);

        model.addAttribute("study", study);
        model.addAttribute("sessionBean", sessionDao.get(id));
        SubjectsBean subjectsBean = new SubjectsBean();
        subjectsBean.setSubjectList(sessionService.listSubjectsBySessionId(id));
        model.addAttribute("subjectsBean", subjectsBean);

        return "session/session-display";
    }

    @GetMapping("/admin/studies/{studyId}/sessions/create")
    public String createSession(@PathVariable("studyId") Long studyId, Model model) {
        SessionBean session = new SessionBean();

        session.setSessionHasTaskGroupRelationshipBean(new SessionHasTaskGroupRelationshipBean());
        Study study = studyDao.get(studyId);
        session.setStudyId(study.getId());

        Condition condition = new Condition();
        condition.setConditionName(DEFAULT_CONDITION_NAME);
        condition.setStudyId(studyId);
        model.addAttribute("study", study);
        model.addAttribute("sessionBean", session);
        return "session/session-edit";
    }

    @GetMapping("/admin/studies/{studyId}/sessions/{id}/edit")
    public String editSession(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id, Model model) {
        SessionBean session = new SessionBean(sessionDao.get(id));
        Study study = studyDao.get(studyId);
        session.setStudyId(study.getId());

        session.setSessionHasTaskGroupRelationshipBean(new SessionHasTaskGroupRelationshipBean());
        session.getSessionHasTaskGroupRelationshipBean().setSessionHasTaskGroupSelectedValues(
                sessionService.listSessionHasTaskGroupBySessionId(session.getId()));
        model.addAttribute("study", study);
        model.addAttribute("sessionBean", session);
        return "session/session-edit";
    }

    @PostMapping("/admin/sessions/subjects/edit")
    public String saveSubject(@ModelAttribute SubjectsBean subjectsBean, RedirectAttributes redirectAttributes) {

            sessionService.updateSubjectList(subjectsBean);
        return "redirect:/admin/studies/"+subjectsBean.getStudyId()+"/sessions/" + subjectsBean.getSessionId();
    }
    @PostMapping("/admin/sessions")
    public String saveSession(@ModelAttribute SessionBean sessionBean, RedirectAttributes redirectAttributes) {

        sessionService.createOrUpdate(sessionBean);
        if (sessionBean.getId() == null) {
            MessageUtils.addSuccessMessage("Session created successfully!", redirectAttributes);
        } else {
            MessageUtils.addSuccessMessage("Session updated successfully!", redirectAttributes);
        }

        return "redirect:/admin/studies/" + sessionBean.getStudyId();
    }

    @GetMapping("/admin/studies/{studyId}/sessions/{id}/subjects/edit")
    public String editSubjects(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id, Model model) {
        SessionBean session = new SessionBean(sessionDao.get(id));
        Study study = studyDao.get(studyId);
        session.setStudyId(study.getId());
        SubjectsBean subjectsBean = new SubjectsBean();
        subjectsBean.setSubjectList(sessionService.listSubjectsBySessionId(session.getId()));
        model.addAttribute("study", study);
        model.addAttribute("sessionBean", session);
        model.addAttribute("subjectsBean", subjectsBean);
        return "session/subject-edit";
    }
}
