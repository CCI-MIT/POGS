package edu.mit.cci.pogs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.cron.SessionCron;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.sessionhastaskgroup.SessionHasTaskGroupDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.runner.SessionRunner;
import edu.mit.cci.pogs.view.session.beans.SessionBean;
import edu.mit.cci.pogs.view.session.beans.SubjectsBean;

@Service
public class SessionService {


    private final SessionHasTaskGroupDao sessionHasTaskGroupDao;
    private final SessionDao sessionDao;
    private final SubjectDao subjectDao;

    private static final Logger _log = LoggerFactory.getLogger(SessionService.class);

    public static final long WAITING_ROOM_OPEN_INIT_WINDOW = 15*60_000;//15 mins

    @Autowired
    private ApplicationContext context;

    @Autowired
    public SessionService(SessionHasTaskGroupDao sessionHasTaskGroupDao, SessionDao sessionDao,
                          SubjectDao subjectdao) {
        this.sessionHasTaskGroupDao = sessionHasTaskGroupDao;
        this.sessionDao = sessionDao;
        this.subjectDao = subjectdao;
    }

    public List<SessionHasTaskGroup> listSessionHasTaskGroupBySessionId(Long sessionid) {
        return sessionHasTaskGroupDao.listSessionHasTaskGroupBySessionId(sessionid);
    }

    public void initializeSessionRunners(){
        List<Session> sessions = getSessionsToBeInitialized();
        for(Session s: sessions){
            //place to create the new session runner thread
            _log.debug(" Session runner starting: " + s.getSessionSuffix());
            SessionRunner sessionRunner = (SessionRunner) context.getBean("sessionRunner");
            sessionRunner.setSession(s);
            SessionRunner.addSessionRunner(s.getId(),sessionRunner);
            sessionRunner.run();


        }
    }
    public List<Session> getSessionsToBeInitialized(){
        return sessionDao.listStartsIn(WAITING_ROOM_OPEN_INIT_WINDOW);
    }


    public Session createOrUpdate(SessionBean sessionBean) {
        Session session = new Session();
        session.setId(sessionBean.getId());
        session.setSessionSuffix(sessionBean.getSessionSuffix());
        session.setSessionStartDate(sessionBean.getSessionStartDate());
        session.setConditionId(sessionBean.getConditionId());
        session.setStatus(sessionBean.getStatus());
        session.setWaitingRoomTime(sessionBean.getWaitingRoomTime());
        session.setIntroPageEnabled(sessionBean.getIntroPageEnabled());
        session.setIntroText(sessionBean.getIntroText());
        session.setIntroTime(sessionBean.getIntroTime());
        session.setDisplayNameChangePageEnabled(sessionBean.getDisplayNameChangePageEnabled());
        session.setDisplayNameChangeTime(sessionBean.getDisplayNameChangeTime());
        session.setRosterPageEnabled(sessionBean.getRosterPageEnabled());
        session.setRosterTime(sessionBean.getRosterTime());
        session.setDonePageEnabled(sessionBean.getDonePageEnabled());
        session.setDonePageText(sessionBean.getDonePageText());
        session.setDonePageTime(sessionBean.getDonePageTime());
        session.setDoneRedirectUrl(sessionBean.getDoneRedirectUrl());
        session.setCouldNotAssignToTeamMessage(sessionBean.getCouldNotAssignToTeamMessage());
        session.setTaskExecutionType(sessionBean.getTaskExecutionType());
        session.setRoundsEnabled(sessionBean.getRoundsEnabled());
        session.setNumberOfRounds(sessionBean.getNumberOfRounds());
        session.setCommunicationType(sessionBean.getCommunicationType());
        session.setChatBotName(sessionBean.getChatBotName());
        session.setScoreboardEnabled(sessionBean.getScoreboardEnabled());
        session.setScoreboardDisplayType(sessionBean.getScoreboardDisplayType());
        session.setScoreboardUseDisplayNames(sessionBean.getScoreboardUseDisplayNames());
        session.setCollaborationTodoListEnabled(sessionBean.getCollaborationTodoListEnabled());
        session.setCollaborationFeedbackWidgetEnabled(sessionBean.getCollaborationFeedbackWidgetEnabled());
        session.setCollaborationVotingWidgetEnabled(sessionBean.getCollaborationVotingWidgetEnabled());
        session.setTeamCreationMoment(sessionBean.getTeamCreationMoment());
        session.setTeamCreationType(sessionBean.getTeamCreationType());
        session.setTeamMinSize(sessionBean.getTeamMinSize());
        session.setTeamMaxSize(sessionBean.getTeamMaxSize());
        session.setTeamCreationMethod(sessionBean.getTeamCreationMethod());
        session.setTeamCreationMatrix(sessionBean.getTeamCreationMatrix());

        if (sessionBean.getId() == null) {
            session = sessionDao.create(session);
            sessionBean.setId(session.getId());
            createOrUpdateSessionHasTaskGroups(sessionBean);
        } else {
            sessionDao.update(session);
            createOrUpdateSessionHasTaskGroups(sessionBean);
        }

        return session;
    }

    private void createOrUpdateSessionHasTaskGroups(SessionBean studyBean) {
        if (studyBean.getSessionHasTaskGroupRelationshipBean() == null && studyBean.getSessionHasTaskGroupRelationshipBean().getSelectedValues() == null) {
            return;
        }
        List<SessionHasTaskGroup> toCreate = new ArrayList<>();
        List<SessionHasTaskGroup> toDelete = new ArrayList<>();
        List<SessionHasTaskGroup> currentlySelected = listSessionHasTaskGroupBySessionId(studyBean.getId());

        for (SessionHasTaskGroup rghau : currentlySelected) {
            boolean foundRGH = false;
            for (String researchGroupId : studyBean.getSessionHasTaskGroupRelationshipBean().getSelectedValues()) {
                if (rghau.getTaskGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    foundRGH = true;
                }
            }
            if (!foundRGH) {
                toDelete.add(rghau);
            }

        }

        for (String taskGroupId : studyBean.getSessionHasTaskGroupRelationshipBean().getSelectedValues()) {

            boolean selectedAlreadyIn = false;
            for (SessionHasTaskGroup rghau : currentlySelected) {
                if (rghau.getTaskGroupId().longValue() == new Long(taskGroupId).longValue()) {
                    selectedAlreadyIn = true;
                }
            }
            if (!selectedAlreadyIn) {
                SessionHasTaskGroup rghau = new SessionHasTaskGroup();
                rghau.setSessionId(studyBean.getId());
                rghau.setTaskGroupId(new Long(taskGroupId));
                toCreate.add(rghau);
            }

        }
        for (SessionHasTaskGroup toCre : toCreate) {
            sessionHasTaskGroupDao.create(toCre);
        }
        for (SessionHasTaskGroup toDel : toDelete) {
            sessionHasTaskGroupDao.delete(toDel);
        }

    }

    public List<Session> listSessionByConditionId(Long conditionId) {
        return sessionDao.listByConditionId(conditionId);
    }

    public List<Subject> listSubjectsBySessionId(Long sessionId) {
        return subjectDao.listBySessionId(sessionId);
    }


    public void updateSubjectList(SubjectsBean subjectsBean) {
        List<Subject> subjectList = subjectsBean.getSubjectList();
        for (Subject subject : subjectList) {
            subject.setSessionId(subjectsBean.getSessionId());
            if(subject.getId()!=null){
                subjectDao.update(subject);
            }else{
                subjectDao.create(subject);
            }
        }
    }


}
