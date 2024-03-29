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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.cci.pogs.config.AuthUserDetailsService;
import edu.mit.cci.pogs.model.dao.chatchannel.ChatChannelDao;
import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.dao.executablescript.ScriptType;
import edu.mit.cci.pogs.model.dao.session.*;
import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.dao.subject.NameGenerationType;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.subjectattribute.SubjectAttributeDao;
import edu.mit.cci.pogs.model.dao.subjectcommunication.SubjectCommunicationDao;
import edu.mit.cci.pogs.model.dao.subjecthaschannel.SubjectHasChannelDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskgroup.TaskGroupDao;
import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.jooq.tables.pojos.*;
import edu.mit.cci.pogs.runner.ScoringRunner;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.runner.SessionRunnerManager;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.service.ChatChannelService;
import edu.mit.cci.pogs.service.CompletedTaskScoreService;
import edu.mit.cci.pogs.service.SessionService;
import edu.mit.cci.pogs.service.SubjectCommunicationService;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.utils.SqlTimestampPropertyEditor;
import edu.mit.cci.pogs.view.session.beans.*;

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

    @Autowired
    private SubjectHasChannelDao subjectHasChannelDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private SubjectCommunicationDao subjectCommunicationDao;

    @Autowired
    private ChatChannelDao chatChannelDao;

    @Autowired
    private CompletedTaskScoreService completedTaskScoreService;

    @Autowired
    private ChatChannelService chatChannelService;

    @Autowired
    private SubjectCommunicationService subjectCommunicationService;

    @Autowired
    private SubjectAttributeDao subjectAttributeDao;

    @Autowired
    private ExecutableScriptDao executableScriptDao;

    private static final String DEFAULT_CONDITION_NAME = "";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Timestamp.class, new SqlTimestampPropertyEditor());
    }

    @ModelAttribute("taskGroups")
    public List<TaskGroup> getTaskGroups() {

        return taskGroupDao.listTaskGroupsWithUserGroup(AuthUserDetailsService.getLoggedInUser());
    }

    @ModelAttribute("sessionScheduleConditionToStart")
    public List<SessionScheduleConditionToStartType> getSessionScheduleConditionToStart() {

        return Arrays.asList(SessionScheduleConditionToStartType.values());
    }

    @ModelAttribute("sessionScheduleType")
    public List<SessionScheduleType> getSessionScheduleType() {

        return Arrays.asList(SessionScheduleType.values());
    }

    @ModelAttribute("nameGenerationType")
    public List<NameGenerationType> getNameGenerationType() {

        return Arrays.asList(NameGenerationType.values());
    }

    @ModelAttribute("executableScripts")
    public List<ExecutableScript> getExecutableScripts() {
        return executableScriptDao.listByScriptTypeWithUserGroup(ScriptType.PERPETUAL_INIT_CONDITION,
                AuthUserDetailsService.getLoggedInUser());
    }

    @ModelAttribute("sessionWideExecutableScripts")
    public List<ExecutableScript> getSessionWideExecutableScripts() {
        return executableScriptDao.listByScriptTypeWithUserGroup(ScriptType.SESSION_WIDE_OVERRIDE,
                AuthUserDetailsService.getLoggedInUser());
    }

    @ModelAttribute("recordedSessionExecutableScripts")
    public List<ExecutableScript> gerecordedSessionExecutableScripts() {
        return executableScriptDao.listByScriptTypeWithUserGroup(ScriptType.RECORDED_SESSION_SCRIPT,
                AuthUserDetailsService.getLoggedInUser());
    }

    @ModelAttribute("sessionBeforeExecutableScripts")
    public List<ExecutableScript> getSessionBeforeExecutableScripts() {
        return executableScriptDao.listByScriptTypeWithUserGroup(ScriptType.SESSION_BEFORE_START,
                AuthUserDetailsService.getLoggedInUser());
    }
    @ModelAttribute("sessionAfterExecutableScripts")
    public List<ExecutableScript> getSessionAfterExecutableScripts() {
        return executableScriptDao.listByScriptTypeWithUserGroup(ScriptType.SESSION_AFTER_END,
                AuthUserDetailsService.getLoggedInUser());
    }

    @ModelAttribute("sessionScheduleConditionType")
    public List<SessionScheduleConditionToStartType> getSessionScheduleConditionToStartType() {

        return Arrays.asList(SessionScheduleConditionToStartType.values());
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
        model.addAttribute("sessionBean", new SessionBean(sessionDao.get(id)));
        SubjectsBean subjectsBean = new SubjectsBean();

        List<SubjectBean> sbList = new ArrayList<>();
        for (Subject su : sessionService.listSubjectsBySessionId(id)) {
            sbList.add(new SubjectBean(su));
            //
        }
        for (SubjectBean sb : sbList) {
            subjectsBean.setSubjectList(sbList);
            sb.setSubjectCommunications(subjectCommunicationDao.listByFromSubjectId(sb.getId()));
            sb.setSubjectAttributes(subjectAttributeDao.listBySubjectId(sb.getId()));
        }

        List<ChatChannel> chatChannels = chatChannelDao.listBySessionId(id);
        List<ChatChannelBean> chatChannelBeans = new ArrayList<>();

        for (ChatChannel cc : chatChannels) {
            ChatChannelBean ccb = new ChatChannelBean(cc);
            List<SubjectHasChannel> subjectHasChannels = subjectHasChannelDao.listByChatId(ccb.getId());
            List<Subject> subjectList = new ArrayList<>();
            if (subjectHasChannels != null && subjectHasChannels.size() > 0) {
                for (SubjectHasChannel shc : subjectHasChannels) {
                    Subject subject = subjectDao.get(shc.getSubjectId());
                    subjectList.add(subject);
                }
            }
            ccb.setSubjectList(subjectList);
            ccb.setSubjectHasChannelList(subjectHasChannels);
            chatChannelBeans.add(ccb);
        }
        model.addAttribute("chatChannelBeans", chatChannelBeans);

        model.addAttribute("subjectsBean", subjectsBean);

        return "session/session-display";
    }

    @GetMapping("/admin/studies/{studyId}/sessions/{id}/score")
    public String getScores(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id, Model model) {
        Study study = studyDao.get(studyId);

        model.addAttribute("study", study);
        model.addAttribute("sessionBean", new SessionBean(sessionDao.get(id)));

        List<CompletedTask> completedTaskList = sessionService.listCompletedTasksOfSession(id);
        List<CompletedTaskScore> completedTaskScores = completedTaskScoreService.listCompletedTaskScore(completedTaskList);
        Map<Long, CompletedTask> completedTaskMap = new HashMap<>();
        completedTaskList.stream().forEach(c -> completedTaskMap.put(c.getId(), c));
        List<ScoreBean> scoreBeans = new ArrayList<>();
        for(CompletedTaskScore cts: completedTaskScores){
            ScoreBean scoreBean = new ScoreBean();
            Task task = taskDao.get(completedTaskMap.get(cts.getCompletedTaskId()).getTaskId());
            scoreBean.setId(cts.getId());
            scoreBean.setTaskId(task.getId());
            scoreBean.setTaskName(task.getTaskName());
            scoreBean.setNumberOfEntries(cts.getNumberOfEntries());

            scoreBean.setNumberOfProcessedEntries(cts.getNumberOfProcessedEntries());
            scoreBean.setNumberOfRightAnswers(cts.getNumberOfRightAnswers());
            scoreBean.setNumberOfWrongAnswers(cts.getNumberOfWrongAnswers());
            scoreBean.setTotalScore(cts.getTotalScore());
            scoreBean.setScoringData(cts.getScoringData());

            scoreBeans.add(scoreBean);
        }

        model.addAttribute("scoreBeans", scoreBeans);

        return "session/session-score";
    }

    @GetMapping("/admin/studies/{studyId}/sessions/create")
    public String createSession(@PathVariable("studyId") Long studyId, Model model) {
        SessionBean session = new SessionBean();
        session.setRoundsEnabled(true);
        session.setNumberOfRounds(1);
        session.setWaitingRoomTime(5*60);
        session.setStatus(SessionStatus.NOTSTARTED.getId() + "");
        session.setSessionHasTaskGroupRelationshipBean(new SessionHasTaskGroupRelationshipBean());
        Study study = studyDao.get(studyId);
        session.setStudyId(study.getId());

        setupRecordedSessions(studyId, model, session);

        model.addAttribute("study", study);
        model.addAttribute("sessionBean", session);
        return "session/session-edit";
    }


    @PostMapping("/admin/studies/{studyId}/sessions/{id}/reset")
    public String resetSession(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id) {
        Session session = (sessionDao.get(id));
        Study study = studyDao.get(studyId);
        sessionService.resetSession(session);

        return "redirect:/admin/studies/" + study.getId() + "/sessions/" + session.getId();
    }


    @GetMapping("/admin/studies/{studyId}/sessions/{id}/rescore")
    public String rescoreSessionGet(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
      return rescoreSession(studyId,id,redirectAttributes);
    }
    @PostMapping("/admin/studies/{studyId}/sessions/{id}/rescore")
    public String rescoreSession(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Session session = (sessionDao.get(id));
        Study study = studyDao.get(studyId);
        sessionService.rescoreSession(session);


        MessageUtils.addSuccessMessage("Session score initiated successfully, please beware it may take a while!", redirectAttributes);

        return "redirect:/admin/studies/" + study.getId() + "/sessions/" + session.getId();
    }


    @GetMapping("/admin/studies/{studyId}/sessions/{id}/reruntaskafterwork")
    public String reRunTaskAfterWorkSession(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Session session = (sessionDao.get(id));
        Study study = studyDao.get(studyId);
        sessionService.reRunTaskAfterWorkInSession(session);


        MessageUtils.addSuccessMessage("Session after work scripts initiated successfully, please beware it may take a while!", redirectAttributes);

        return "redirect:/admin/studies/" + study.getId() + "/sessions/" + session.getId();
    }


    @GetMapping("/admin/studies/{studyId}/sessions/{id}/edit")
    public String editSession(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id, Model model) {
        SessionBean session = new SessionBean(sessionDao.get(id));
        session.setRoundsEnabled(true);
        session.setNumberOfRounds(1);

        Study study = studyDao.get(studyId);
        session.setStudyId(study.getId());

        session.setSessionHasTaskGroupRelationshipBean(new SessionHasTaskGroupRelationshipBean());
        session.getSessionHasTaskGroupRelationshipBean().setSessionHasTaskGroupSelectedValues(
                sessionService.listSessionHasTaskGroupBySessionId(session.getId()));
        model.addAttribute("study", study);
        model.addAttribute("sessionBean", session);


        setupRecordedSessions(studyId, model, session);

        return "session/session-edit";
    }

    private void setupRecordedSessions(Long studyId, Model model, SessionBean session) {
        List<Session> recordedSessions = new ArrayList<>();
        if(session.getId()== null) {
            recordedSessions = sessionDao.listByStudyId(studyId);
        } else {
            for (Session s: sessionDao.listByStudyId(studyId)) {
                if(s.getId()!= session.getId()) {
                    recordedSessions.add(s);
                }
            }
        }
        model.addAttribute("previousRecordedSessions",recordedSessions);
    }

    @PostMapping("/admin/sessions/subjects/edit")
    public String saveSubject(@ModelAttribute SubjectsBean subjectsBean, RedirectAttributes redirectAttributes) {

        sessionService.updateSubjectList(subjectsBean);
        return "redirect:/admin/studies/" + subjectsBean.getStudyId() + "/sessions/" + subjectsBean.getSessionId();
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
        List<SubjectBean> sbList = new ArrayList<>();
        for (Subject su : sessionService.listSubjectsBySessionId(session.getId())) {
            SubjectBean subjectBean = new SubjectBean(su);
            List<SubjectAttribute> subjectAttributes = subjectAttributeDao.listBySubjectId(su.getId());
            subjectBean.setSubjectAttributes(subjectAttributes);
            sbList.add(subjectBean);

        }
        subjectsBean.setSubjectList(sbList);


        model.addAttribute("study", study);
        model.addAttribute("sessionBean", session);
        model.addAttribute("subjectsBean", subjectsBean);
        return "session/subject-edit";
    }

    @PostMapping("/admin/sessions/chatchannels/edit")
    public String saveChatChannel(@ModelAttribute ChatChannelBean chatChannelBean, RedirectAttributes redirectAttributes) {

        if (chatChannelBean.getId() == null) {
            ChatChannel cc = chatChannelDao.create(chatChannelBean);
            chatChannelBean.setId(cc.getId());
        } else {
            chatChannelDao.update(chatChannelBean);
        }
        chatChannelService.updateOrCreateChat(chatChannelBean);

        return "redirect:/admin/studies/" + chatChannelBean.getStudyId() + "/sessions/" + chatChannelBean.getSessionId();
    }

    @GetMapping("/admin/studies/{studyId}/sessions/{id}/chatchannels/create")
    public String createChatChannel(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id,
                                    Model model) {
        SessionBean session = new SessionBean(sessionDao.get(id));
        Study study = studyDao.get(studyId);
        ChatChannelBean chatChannelBean = new ChatChannelBean();
        chatChannelBean.setSubjectHasChannelList(new ArrayList<>());
        chatChannelBean.setSubjectList(new ArrayList<>());
        chatChannelBean.setSessionId(session.getId());

        SubjectsBean subjectsBean = new SubjectsBean();
        List<SubjectBean> sbList = new ArrayList<>();
        for (Subject su : sessionService.listSubjectsBySessionId(session.getId())) {
            sbList.add(new SubjectBean(su));
        }
        subjectsBean.setSubjectList(sbList);

        model.addAttribute("study", study);
        model.addAttribute("sessionBean", session);
        model.addAttribute("chatChannelBean", chatChannelBean);
        model.addAttribute("subjectsBean", subjectsBean);

        return "session/chatchannel-edit";

    }


    @GetMapping("/admin/studies/{studyId}/sessions/{id}/chatchannels/{channelId}/delete")
    public String deleteChatChannel(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id,
                                  @PathVariable("channelId") Long channelId, Model model) {

        SessionBean session = new SessionBean(sessionDao.get(id));
        Study study = studyDao.get(studyId);
        ChatChannel cc = chatChannelDao.get(channelId);
        chatChannelService.delete(cc);

        return "redirect:/admin/studies/" + study.getId() + "/sessions/" + session.getId();
    }
    @GetMapping("/admin/studies/{studyId}/sessions/{id}/chatchannels/{channelId}/edit")
    public String editChatChannel(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id,
                                  @PathVariable("channelId") Long channelId, Model model) {

        SessionBean session = new SessionBean(sessionDao.get(id));
        Study study = studyDao.get(studyId);
        ChatChannel cc = chatChannelDao.get(channelId);
        ChatChannelBean chatChannelBean = new ChatChannelBean(cc);
        List<SubjectHasChannel> subjectHasChannels = subjectHasChannelDao.listByChatId(chatChannelBean.getId());
        List<Subject> subjectList = new ArrayList<>();
        if (subjectHasChannels != null && subjectHasChannels.size() > 0) {
            for (SubjectHasChannel shc : subjectHasChannels) {
                Subject subject = subjectDao.get(shc.getSubjectId());
                subjectList.add(subject);
            }
        }
        chatChannelBean.setSubjectList(subjectList);
        chatChannelBean.setSubjectHasChannelList(subjectHasChannels);

        SubjectsBean subjectsBean = new SubjectsBean();
        List<SubjectBean> sbList = new ArrayList<>();
        for (Subject su : sessionService.listSubjectsBySessionId(session.getId())) {
            sbList.add(new SubjectBean(su));
        }
        subjectsBean.setSubjectList(sbList);

        model.addAttribute("study", study);
        model.addAttribute("sessionBean", session);
        model.addAttribute("chatChannelBean", chatChannelBean);
        model.addAttribute("subjectsBean", subjectsBean);

        return "session/chatchannel-edit";
    }

    @GetMapping("/admin/studies/{studyId}/sessions/{id}/subjects/editCommunication")
    public String editCommunicationSubjects(@PathVariable("studyId") Long studyId, @PathVariable("id") Long id, Model model) {
        SessionBean session = new SessionBean(sessionDao.get(id));
        Study study = studyDao.get(studyId);
        session.setStudyId(study.getId());
        SubjectsBean subjectsBean = new SubjectsBean();
        List<SubjectBean> sbList = new ArrayList<>();
        for (Subject su : sessionService.listSubjectsBySessionId(session.getId())) {
            sbList.add(new SubjectBean(su));
            //
        }
        for (SubjectBean sb : sbList) {
            subjectsBean.setSubjectList(sbList);
            sb.setSubjectCommunications(subjectCommunicationDao.listByFromSubjectId(sb.getId()));
            sb.setSubjectAttributes(subjectAttributeDao.listBySubjectId(sb.getId()));
        }
        model.addAttribute("study", study);
        model.addAttribute("sessionBean", session);
        model.addAttribute("subjectsBean", subjectsBean);
        return "session/subject-communication-edit";
    }


    @PostMapping("/admin/sessions/subjects/editCommunication")
    public String saveCommunicationSubject(@ModelAttribute SubjectCommunicationBean subjectCommunicationBean, RedirectAttributes redirectAttributes) {
        Session session = sessionDao.get(subjectCommunicationBean.getSessionId());
        Study study = studyDao.get(subjectCommunicationBean.getStudyId());
        subjectCommunicationService.updateSubjectCommunications(subjectCommunicationBean);
        return "redirect:/admin/studies/" + study.getId() + "/sessions/" + session.getId();
    }

}
