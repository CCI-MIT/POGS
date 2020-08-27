package edu.mit.cci.pogs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.subjecthassessioncheckin.SubjectHasSessionCheckInDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasSessionCheckIn;
import edu.mit.cci.pogs.utils.DateUtils;
import edu.mit.cci.pogs.view.dashboard.SubjectHasSessionCheckInBean;

@Service
public class SubjectHasSessionCheckInService {

    @Autowired
    private SubjectHasSessionCheckInDao subjectHasSessionCheckInDao;

    @Autowired
    private SubjectDao subjectDao;

    static final Integer ACCEPTABLETIMETHRESHOLD = 20*1000;

    public void checkSubjectIn(Subject su, Session session) {
        SubjectHasSessionCheckIn shsc = subjectHasSessionCheckInDao.getBySubjectIdSessionId(su.getId(), session.getId());
        if (shsc == null) {
            shsc = new SubjectHasSessionCheckIn();
            shsc.setSubjectId(su.getId());
            shsc.setSessionId(session.getId());
        }

        Long now = DateUtils.now();
        shsc.setCheckInTime(new Timestamp(now));
        shsc.setHasJoinedSession(false);
        shsc.setHasLostSession(false);
        shsc.setShouldExpireTime(new Timestamp(now + DateUtils.toMilliseconds(session.getWaitingRoomTime())));
        shsc.setLastPingTime(new Timestamp(now));
        shsc.setJoinedSessionTime(null);
        shsc.setLostSubjectTime(null);

        if (shsc.getId() == null) {
            subjectHasSessionCheckInDao.create(shsc);
        } else {
            subjectHasSessionCheckInDao.update(shsc);
        }
    }
    public Long getSubjectSessionCheckInExpireTime(Long subjectId, Long sessionId){
        SubjectHasSessionCheckIn shsc = subjectHasSessionCheckInDao.getBySubjectIdSessionId(subjectId, sessionId);
        if(shsc!=null){
            return shsc.getShouldExpireTime().getTime();
        } else {
            return 0l;
        }
    }
    public void subjectJoinedSession(SubjectHasSessionCheckIn subjectCheckInTicket){
        subjectCheckInTicket.setJoinedSessionTime(new Timestamp(DateUtils.now()));
        subjectCheckInTicket.setHasJoinedSession(true);
        subjectHasSessionCheckInDao.update(subjectCheckInTicket);
    }

    public boolean hasSubjectExpiredOrNotPingedRecently(SubjectHasSessionCheckIn subjectHasSessionCheckIn){
        Long now = DateUtils.now();

        if(subjectHasSessionCheckIn.getShouldExpireTime().getTime() < now ||
                !(subjectHasSessionCheckIn.getLastPingTime().getTime() > (now - ACCEPTABLETIMETHRESHOLD))
        ){
            subjectHasSessionCheckIn.setLostSubjectTime(new Timestamp(now));
            subjectHasSessionCheckIn.setHasLostSession(true);
            subjectHasSessionCheckInDao.update(subjectHasSessionCheckIn);
            return true;
        }

        return false;
    }
    public void updateLatestSubjectPing(Long subjectId, Long sessionId) {
        SubjectHasSessionCheckIn shsc = subjectHasSessionCheckInDao.getBySubjectIdSessionId(subjectId, sessionId);
        if(shsc!= null) {
            shsc.setLastPingTime(new Timestamp(DateUtils.now()));
            subjectHasSessionCheckInDao.update(shsc);
        }
    }

    private List<SubjectHasSessionCheckInBean> checkInBeanFromSubjectHasSessionCheckInBean(List<SubjectHasSessionCheckIn> list){
        List<SubjectHasSessionCheckInBean> ret = new ArrayList<>();
        for(SubjectHasSessionCheckIn lhsc: list){
            SubjectHasSessionCheckInBean shscci = new SubjectHasSessionCheckInBean(lhsc);
            shscci.setSubject(subjectDao.get(lhsc.getSubjectId()));
            ret.add(shscci);
        }
        return ret;
    }
    public List<SubjectHasSessionCheckIn> listReadyToJoinSubjects(Long sessionId) {
        return subjectHasSessionCheckInDao.listReadyToJoinSubjects(sessionId);
    }

    public List<SubjectHasSessionCheckInBean> listReadyToJoinSubjectsBean(Long sessionId) {
        return checkInBeanFromSubjectHasSessionCheckInBean(subjectHasSessionCheckInDao.listReadyToJoinSubjects(sessionId));
    }

    public List<SubjectHasSessionCheckInBean> listLostSubjects(Long sessionId){
        return checkInBeanFromSubjectHasSessionCheckInBean(subjectHasSessionCheckInDao.listLostSubjects(sessionId));
    }

    public List<SubjectHasSessionCheckInBean> listCheckedInSubjects(Long sessionId){
        return checkInBeanFromSubjectHasSessionCheckInBean(subjectHasSessionCheckInDao.listCheckedInSubjects(sessionId));
    }

}
