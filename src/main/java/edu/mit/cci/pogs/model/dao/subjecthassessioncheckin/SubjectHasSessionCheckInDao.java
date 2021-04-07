package edu.mit.cci.pogs.model.dao.subjecthassessioncheckin;

import java.util.Date;
import java.util.List;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasSessionCheckIn;

public interface SubjectHasSessionCheckInDao extends Dao<SubjectHasSessionCheckIn, Long> {

    List<SubjectHasSessionCheckIn> listLostSubjects(Long sessionId, Date date);

    List<SubjectHasSessionCheckIn> listCheckedInSubjects(Long sessionId, Date date);

    List<SubjectHasSessionCheckIn> listReadyToJoinSubjects(Long sessionId);

    SubjectHasSessionCheckIn getBySubjectIdSessionId(Long subjectId, Long sessionId);
}
