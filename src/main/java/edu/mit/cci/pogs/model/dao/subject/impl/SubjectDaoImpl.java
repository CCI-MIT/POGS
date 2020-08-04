package edu.mit.cci.pogs.model.dao.subject.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.records.SubjectRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.SESSION;
import static edu.mit.cci.pogs.model.jooq.Tables.SESSION_HAS_TASK_GROUP;
import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT;
import static edu.mit.cci.pogs.model.jooq.Tables.TEAM;
import static edu.mit.cci.pogs.model.jooq.Tables.TEAM_HAS_SUBJECT;

@Repository
public class SubjectDaoImpl extends AbstractDao<Subject, Long, SubjectRecord> implements SubjectDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public SubjectDaoImpl(DSLContext dslContext) {
        super(dslContext, SUBJECT, SUBJECT.ID, Subject.class);
        this.dslContext = dslContext;
    }
 
    public List<Subject> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT).getQuery();
 
        return query.fetchInto(Subject.class);
    }
    public List<Subject> listBySessionId( Long sessionId){
        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT).getQuery();
        query.addConditions(SUBJECT.SESSION_ID.eq(sessionId));
        return query.fetchInto(Subject.class);
    }

    public List<Subject> listBySessionList( List<Long> sessionList){

        edu.mit.cci.pogs.model.jooq.tables.Subject subject = edu.mit.cci.pogs.model.jooq.tables.Subject.SUBJECT.as("su");

        final SelectQuery<Record> query = dslContext.select(subject.fields())
                .from(subject)
                .getQuery();

        query.addConditions(subject.SESSION_ID.in(sessionList));

        return query.fetchInto(Subject.class);
    }

    public List<Subject> listBySessionIdOrParentSessionId( Long sessionId){

        edu.mit.cci.pogs.model.jooq.tables.Subject subject = edu.mit.cci.pogs.model.jooq.tables.Subject.SUBJECT.as("su");
        edu.mit.cci.pogs.model.jooq.tables.Session session = Session.SESSION.as("se");
        final SelectQuery<Record> query = dslContext.select(subject.fields())
                .from(subject)
                .innerJoin(session).on(session.ID.eq(subject.SESSION_ID))
                .getQuery();

        query.addConditions(session.ID.eq(sessionId).or(session.PARENT_SESSION_ID.eq(sessionId)));

        return query.fetchInto(Subject.class);
    }

    public Subject getByExternalId(String externalId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT).getQuery();
        query.addConditions(SUBJECT.SUBJECT_EXTERNAL_ID.eq(externalId));
        Record record =  query.fetchAny();
        if(record == null) {
            return null;
        }else{
            return record.into(Subject.class);
        }
    }

    public Subject getByPogsUniqueHashId(String pogsUniqueHash) {
        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT).getQuery();
        query.addConditions(SUBJECT.POGS_UNIQUE_HASH.eq(pogsUniqueHash));
        Record record =  query.fetchAny();
        if(record == null) {
            return null;
        }else{
            return record.into(Subject.class);
        }
    }

    //select * from subject where session_id in(
    //select id from session where study_id = 24
    //) order by id;
    public List<Subject> getTeammates(Long sessionId, Long roundId, Long taskId) {
        final SelectQuery<Record> query = dslContext.select(SUBJECT.fields())
                .from(SUBJECT)
                .join(TEAM_HAS_SUBJECT)
                .on(TEAM_HAS_SUBJECT.SUBJECT_ID.eq(SUBJECT.ID))
                .join(TEAM)
                .on(TEAM.ID.eq(TEAM_HAS_SUBJECT.TEAM_ID))
                .getQuery();

        if(sessionId != null) {
            query.addConditions(TEAM.SESSION_ID.eq(sessionId));
        }

        if(roundId != null) {
            query.addConditions(TEAM.ROUND_ID.eq(roundId));
        }
        if(taskId != null) {
            query.addConditions(TEAM.TASK_ID.eq(taskId));
        }
        return query.fetchInto(Subject.class);
    }
    public void delete(Long subjectId) {
        dslContext.delete(SUBJECT)
                .where(SUBJECT.ID.eq(subjectId))
                .execute();

    }

}
 
