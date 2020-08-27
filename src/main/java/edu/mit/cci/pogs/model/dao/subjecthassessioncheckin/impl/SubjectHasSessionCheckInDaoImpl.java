package edu.mit.cci.pogs.model.dao.subjecthassessioncheckin.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.subjecthassessioncheckin.SubjectHasSessionCheckInDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasSessionCheckIn;
import edu.mit.cci.pogs.model.jooq.tables.records.SubjectHasSessionCheckInRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT;
import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT_HAS_SESSION_CHECK_IN;

@Repository
public class SubjectHasSessionCheckInDaoImpl extends AbstractDao<SubjectHasSessionCheckIn, Long, SubjectHasSessionCheckInRecord>  implements SubjectHasSessionCheckInDao {

    private final DSLContext dslContext;

    @Autowired
    public SubjectHasSessionCheckInDaoImpl(DSLContext dslContext) {
        super(dslContext, SUBJECT_HAS_SESSION_CHECK_IN, SUBJECT_HAS_SESSION_CHECK_IN.ID, SubjectHasSessionCheckIn.class);
        this.dslContext = dslContext;
    }

    public List<SubjectHasSessionCheckIn> listLostSubjects(Long sessionId){
        return listSubjects(sessionId, false, true);
    }

    public List<SubjectHasSessionCheckIn> listCheckedInSubjects(Long sessionId){
        return listSubjects(sessionId, true, false);
    }
    public List<SubjectHasSessionCheckIn> listReadyToJoinSubjects(Long sessionId) {
        return listSubjects(sessionId, false, false);
    }
    public List<SubjectHasSessionCheckIn> listSubjects(Long sessionId, boolean hasJoinedSession, boolean hasLostSession) {

        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT_HAS_SESSION_CHECK_IN)
                .getQuery();
        query.addConditions(SUBJECT_HAS_SESSION_CHECK_IN.SESSION_ID.eq(sessionId));
        query.addConditions(SUBJECT_HAS_SESSION_CHECK_IN.HAS_JOINED_SESSION.eq(hasJoinedSession));
        query.addConditions(SUBJECT_HAS_SESSION_CHECK_IN.HAS_LOST_SESSION.eq(hasLostSession));
        query.addOrderBy(SUBJECT_HAS_SESSION_CHECK_IN.CHECK_IN_TIME.asc());
        query.addOrderBy(SUBJECT_HAS_SESSION_CHECK_IN.LAST_PING_TIME.desc());


        return query.fetchInto(SubjectHasSessionCheckIn.class);
    }

    public void subjectJoinedSession(Long subjectId){

    }

    public SubjectHasSessionCheckIn getBySubjectIdSessionId(Long subjectId, Long sessionId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT_HAS_SESSION_CHECK_IN).getQuery();
        query.addConditions(SUBJECT_HAS_SESSION_CHECK_IN.SESSION_ID.eq(sessionId));
        query.addConditions(SUBJECT_HAS_SESSION_CHECK_IN.SUBJECT_ID.eq(subjectId));
        Record record =  query.fetchAny();
        if(record == null) {
            return null;
        }else{
            return record.into(SubjectHasSessionCheckIn.class);
        }
    }

}
