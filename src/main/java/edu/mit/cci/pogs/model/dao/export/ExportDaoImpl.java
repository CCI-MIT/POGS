package edu.mit.cci.pogs.model.dao.export;

import org.jooq.DSLContext;
import org.jooq.Record10;
import org.jooq.Record11;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.SelectQuery;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.mit.cci.pogs.messages.CommunicationMessage;
import edu.mit.cci.pogs.model.jooq.tables.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.CompletedTaskScore;
import edu.mit.cci.pogs.model.jooq.tables.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.IndividualSubjectScore;
import edu.mit.cci.pogs.model.jooq.tables.Round;
import edu.mit.cci.pogs.model.jooq.tables.Session;
import edu.mit.cci.pogs.model.jooq.tables.Study;
import edu.mit.cci.pogs.model.jooq.tables.Subject;
import edu.mit.cci.pogs.model.jooq.tables.Task;

import static edu.mit.cci.pogs.model.jooq.Tables.COMPLETED_TASK;
import static edu.mit.cci.pogs.model.jooq.Tables.COMPLETED_TASK_SCORE;
import static edu.mit.cci.pogs.model.jooq.Tables.EVENT_LOG;
import static edu.mit.cci.pogs.model.jooq.Tables.INDIVIDUAL_SUBJECT_SCORE;
import static edu.mit.cci.pogs.model.jooq.Tables.ROUND;
import static edu.mit.cci.pogs.model.jooq.Tables.SESSION;
import static edu.mit.cci.pogs.model.jooq.Tables.STUDY;
import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT;
import static edu.mit.cci.pogs.model.jooq.Tables.TASK;

@Repository
public class ExportDaoImpl implements ExportDao {

    private final DSLContext dslContext;

    @Autowired
    public ExportDaoImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }


    /*
        select
        st.study_session_prefix,
        s.session_suffix,
        t.task_name,
        t.solo_task,
        c.subject_id,
        c.team_id,
        cts.total_score,
        cts.number_of_entries,
        cts.number_of_processed_entries,
        cts.number_of_right_answers,
        cts.number_of_wrong_answers
                from
        completed_task_score as cts
        inner join completed_task as c on c.id = cts.completed_task_id
        inner join round as r on r.id = c.round_id
        inner join session as s on s.id = r.session_id
        inner join study as st on st.id = s.study_id
        inner join task as t on t.id = c.task_id
     */

    public List<CompletedTaskScoreExport> getCompletedTaskScoreExportInfo(List<Long> sessionIds) {
        CompletedTaskScore cts = COMPLETED_TASK_SCORE.as("cts");
        CompletedTask ct = COMPLETED_TASK.as("ct");
        Task t = TASK.as("t");
        Round r = ROUND.as("r");
        Session s = SESSION.as("s");
        Study st = STUDY.as("st");

        final SelectQuery<Record13<String, String, Timestamp,Long, String, Boolean, Long, Long, Double, Integer, Integer, Integer, Integer>> query = dslContext.select(
                st.STUDY_SESSION_PREFIX.as("studyPrefix"),
                s.SESSION_SUFFIX.as("sessionSuffix"),
                s.SESSION_START_DATE.as("sessionStartDate"),
                s.ID.as("sessionId"),
                t.TASK_NAME.as("taskName"),
                t.SOLO_TASK.as("soloTask"),
                ct.SUBJECT_ID.as("soloSubject"),
                ct.TEAM_ID.as("teamSubjects"),
                cts.TOTAL_SCORE.as("totalScore"),
                cts.NUMBER_OF_ENTRIES.as("numberOfEntries"),
                cts.NUMBER_OF_PROCESSED_ENTRIES.as("numberOfProcesseEntries"),
                cts.NUMBER_OF_RIGHT_ANSWERS.as("numberOfRightAnswers"),
                cts.NUMBER_OF_WRONG_ANSWERS.as("numberOfWrongAnswers")

        )
                .from(cts)
                .innerJoin(ct).on(ct.ID.eq(cts.COMPLETED_TASK_ID))
                .innerJoin(t).on(t.ID.eq(ct.TASK_ID))
                .innerJoin(r).on(r.ID.eq(ct.ROUND_ID))
                .innerJoin(s).on(s.ID.eq(r.SESSION_ID))
                .innerJoin(st).on(st.ID.eq(s.STUDY_ID))
                .getQuery();

        query.addConditions(s.ID.in(sessionIds));


        return query.fetchInto(CompletedTaskScoreExport.class);

    }

    public List<IndividualSubjectScoreExport> getIndividualSubjectScoreExportInfo(List<Long> sessionIds) {
        IndividualSubjectScore cts = INDIVIDUAL_SUBJECT_SCORE.as("cts");
        CompletedTask ct = COMPLETED_TASK.as("ct");
        Task t = TASK.as("t");
        Round r = ROUND.as("r");
        Session s = SESSION.as("s");
        Study st = STUDY.as("st");
        Subject su = SUBJECT.as("su");

        final SelectQuery<Record9<String, String,Timestamp, Long,  String, Boolean, Long, String, Double>> query = dslContext.select(
                st.STUDY_SESSION_PREFIX.as("studyPrefix"),
                s.SESSION_SUFFIX.as("sessionSuffix"),
                s.SESSION_START_DATE.as("sessionStartDate"),
                s.ID.as("sessionId"),
                t.TASK_NAME.as("taskName"),
                t.SOLO_TASK.as("soloTask"),
                su.ID.as("subjectId"),
                su.SUBJECT_EXTERNAL_ID.as("subjectExternalId"),
                cts.INDIVIDUAL_SCORE.as("totalScore")

        )
                .from(cts)
                .innerJoin(ct).on(ct.ID.eq(cts.COMPLETED_TASK_ID))
                .innerJoin(t).on(t.ID.eq(ct.TASK_ID))
                .innerJoin(su).on(su.ID.eq(cts.SUBJECT_ID))
                .innerJoin(r).on(r.ID.eq(ct.ROUND_ID))
                .innerJoin(s).on(s.ID.eq(r.SESSION_ID))
                .innerJoin(st).on(st.ID.eq(s.STUDY_ID))
                .getQuery();

        query.addConditions(s.ID.in(sessionIds));
        query.addOrderBy(ct.START_TIME);


        return query.fetchInto(IndividualSubjectScoreExport.class);

    }

    public List<EventLogExport> getEventLogExportInfo(List<Long> sessionIds) {

        EventLog el = EVENT_LOG.as("el");
        CompletedTask ct = COMPLETED_TASK.as("ct");
        Task t = TASK.as("t");
        Round r = ROUND.as("r");
        Session s = SESSION.as("s");
        Study st = STUDY.as("st");
        Subject su = SUBJECT.as("su");
        final SelectQuery<Record12<String, String,Timestamp,Long, String,Long, String, Boolean, Timestamp, String, String, String>> query = dslContext.select(
                st.STUDY_SESSION_PREFIX.as("studyPrefix"),
                s.SESSION_SUFFIX.as("sessionSuffix"),
                s.SESSION_START_DATE.as("sessionStartDate"),
                s.ID.as("sessionId"),
                su.SUBJECT_EXTERNAL_ID.as("senderSubjectExternalId"),
                el.RECEIVER_SUBJECT_ID.as("receiverId"),
                t.TASK_NAME.as("taskName"),
                t.SOLO_TASK.as("soloTask"),
                el.TIMESTAMP.as("timestamp"),
                el.EVENT_TYPE.as("eventType"),
                el.EVENT_CONTENT.as("eventContent"),
                el.SUMMARY_DESCRIPTION.as("summaryDescription")
        )
                .from(el)
                .innerJoin(ct).on(ct.ID.eq(el.COMPLETED_TASK_ID))
                .innerJoin(t).on(t.ID.eq(ct.TASK_ID))
                .innerJoin(r).on(r.ID.eq(ct.ROUND_ID))
                .innerJoin(s).on(s.ID.eq(r.SESSION_ID))
                .innerJoin(st).on(st.ID.eq(s.STUDY_ID))
                .innerJoin(su).on(su.ID.eq(el.SENDER_SUBJECT_ID))
                .getQuery();

                query.addConditions(s.ID.in(sessionIds));
                query.addConditions(el.COMPLETED_TASK_ID.isNotNull());

        query.addOrderBy(el.TIMESTAMP);
        List<EventLogExport> withCompletedTaskIds = query.fetchInto(EventLogExport.class);

        List<EventLogExport> withoutIds = getEventLogExportNonCompletedTaskInfo(sessionIds);

        withCompletedTaskIds.addAll(withoutIds);
        Collections.sort(withCompletedTaskIds, new EventLogExportComparator());
        return withCompletedTaskIds;

    }
    class EventLogExportComparator implements Comparator<EventLogExport> {
        public int compare(EventLogExport cust1, EventLogExport cust2) {
            return cust1.getTimestamp().compareTo(cust2.getTimestamp());
        }
    }
    public List<EventLogExport> getEventLogExportNonCompletedTaskInfo(List<Long> sessionIds) {

        EventLog el = EVENT_LOG.as("el");

        Session s = SESSION.as("s");
        Study st = STUDY.as("st");
        Subject su = SUBJECT.as("su");
        final SelectQuery<Record8<String, String,Timestamp, String,  Timestamp, String, String, String>>
                query = dslContext.select(
                    st.STUDY_SESSION_PREFIX.as("studyPrefix"),
                    s.SESSION_SUFFIX.as("sessionSuffix"),
                    s.SESSION_START_DATE.as("sessionStartDate"),
                    su.SUBJECT_EXTERNAL_ID.as("senderSubjectExternalId"),
                    el.TIMESTAMP.as("timestamp"),
                    el.EVENT_TYPE.as("eventType"),
                    el.EVENT_CONTENT.as("eventContent"),
                    el.SUMMARY_DESCRIPTION.as("summaryDescription")
                )
                .from(el)
                .innerJoin(s).on(s.ID.eq(el.SESSION_ID))
                .innerJoin(st).on(st.ID.eq(s.STUDY_ID))
                .innerJoin(su).on(su.ID.eq(el.SENDER_SUBJECT_ID))
                .getQuery();

        query.addConditions(el.COMPLETED_TASK_ID.isNull());
        query.addConditions(s.ID.in(sessionIds));

        query.addOrderBy(el.TIMESTAMP);
        return query.fetchInto(EventLogExport.class);

    }
    public List<EventLogCheckingSummary> getEventLogCheckIn(List<Long> sessionIds) {

        EventLog el = EVENT_LOG.as("el");
        CompletedTask ct = COMPLETED_TASK.as("ct");
        Task t = TASK.as("t");
        Round r = ROUND.as("r");
        Session s = SESSION.as("s");
        Study st = STUDY.as("st");
        Subject su = SUBJECT.as("su");
        final SelectQuery<Record11<String, String,Timestamp,Long, Long, String,Long, Long, String,  Timestamp, Integer>> query = dslContext.select(
                st.STUDY_SESSION_PREFIX.as("studyPrefix"),
                s.SESSION_SUFFIX.as("sessionSuffix"),
                s.SESSION_START_DATE.as("sessionStartDate"),
                s.ID.as("sessionId"),
                ct.TEAM_ID.as("teamId"),
                su.SUBJECT_EXTERNAL_ID.as("subjectExternalId"),
                el.COMPLETED_TASK_ID.as("completedTaskId"),
                su.ID.as("subjectId"),
                t.TASK_NAME.as("taskName"),
                el.TIMESTAMP.as("timestamp"),
                DSL.count().as("subjectCount")

        )
                .from(el)
                .innerJoin(ct).on(ct.ID.eq(el.COMPLETED_TASK_ID))
                .innerJoin(t).on(t.ID.eq(ct.TASK_ID))
                .innerJoin(r).on(r.ID.eq(ct.ROUND_ID))
                .innerJoin(s).on(s.ID.eq(r.SESSION_ID))
                .innerJoin(st).on(st.ID.eq(s.STUDY_ID))
                .innerJoin(su).on(su.ID.eq(el.SENDER_SUBJECT_ID))
                .getQuery();

        query.addConditions(s.ID.in(sessionIds));
        query.addConditions(el.COMPLETED_TASK_ID.isNotNull());
        query.addConditions(el.EVENT_TYPE.eq(CommunicationMessage.CommunicationType.CHECK_IN.name()));
        query.addGroupBy(el.SESSION_ID, el.COMPLETED_TASK_ID, el.SENDER_SUBJECT_ID, el.TIMESTAMP);

        List<EventLogCheckingSummary> withCompletedTaskIds = query.fetchInto(EventLogCheckingSummary.class);

        return withCompletedTaskIds;

    }

}
