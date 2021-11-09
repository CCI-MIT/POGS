package edu.mit.cci.pogs.model.dao.completedtask.impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.jooq.tables.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.records.CompletedTaskRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.COMPLETED_TASK;
import static edu.mit.cci.pogs.model.jooq.Tables.ROUND;

@Repository
public class CompletedTaskDaoImpl extends AbstractDao<CompletedTask, Long, CompletedTaskRecord> implements CompletedTaskDao {

    private final DSLContext dslContext;

    @Autowired
    public CompletedTaskDaoImpl(DSLContext dslContext) {
        super(dslContext, COMPLETED_TASK, COMPLETED_TASK.ID, CompletedTask.class);
        this.dslContext = dslContext;
    }

    public List<CompletedTask> list() {

        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK).getQuery();

        return query.fetchInto(CompletedTask.class);
    }

    public List<CompletedTask> listByRoundId(Long roundId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK).getQuery();
        query.addConditions(COMPLETED_TASK.ROUND_ID.eq(roundId));

        return query.fetchInto(CompletedTask.class);

    }

    public List<CompletedTask> listBySessionId(List<Long> sessionIds) {
        edu.mit.cci.pogs.model.jooq.tables.CompletedTask completedTask = COMPLETED_TASK.as("CT");
        Round round = ROUND.as("r");
        final SelectQuery<Record> query = dslContext.select(completedTask.fields())
                .from(completedTask)
                .innerJoin(round).on(round.ID.eq(completedTask.ROUND_ID))
                .getQuery();

        query.addConditions(round.SESSION_ID.in(sessionIds));
        return query.fetchInto(CompletedTask.class);

    }

    public List<CompletedTask> listByRoundIdTeamId(Long roundId, Long teamId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK).getQuery();
        query.addConditions(COMPLETED_TASK.ROUND_ID.eq(roundId));
        query.addConditions(COMPLETED_TASK.TASK_ID.eq(teamId));

        return query.fetchInto(CompletedTask.class);

    }

    public List<CompletedTask> listByRoundIdTaskId(Long roundId, Long taskId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK).getQuery();
        query.addConditions(COMPLETED_TASK.ROUND_ID.eq(roundId));
        query.addConditions(COMPLETED_TASK.TASK_ID.eq(taskId));

        return query.fetchInto(CompletedTask.class);

    }



    public CompletedTask getBySubjectIdTaskId(Long subjectId, Long taskId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK).getQuery();

        query.addConditions(COMPLETED_TASK.SUBJECT_ID.eq(subjectId));
        query.addConditions(COMPLETED_TASK.TASK_ID.eq(taskId));

        Record record = query.fetchAny();
        if (record == null) {
            return null;
        } else {
            return record.into(CompletedTask.class);
        }

    }

    public CompletedTask getByRoundIdTaskIdTeamId(Long roundId, Long teamId, Long taskId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK).getQuery();

        query.addConditions(COMPLETED_TASK.ROUND_ID.eq(roundId));
        query.addConditions(COMPLETED_TASK.TEAM_ID.eq(teamId));
        query.addConditions(COMPLETED_TASK.TASK_ID.eq(taskId));

        List<CompletedTask> record = query.fetchInto(CompletedTask.class);
        if (record == null || record.size()==0) {
            return null;
        } else {
            return record.get(0);
        }

    }

    public void deleteByRoundId(Long roundId){
        dslContext.delete(COMPLETED_TASK)
                .where(COMPLETED_TASK.ROUND_ID.eq(roundId))
                .execute();
    }

    public List<CompletedTask> listByCompletedTaskIds(List<Long> completedTaskIds){
        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK).where(COMPLETED_TASK.ID.in(completedTaskIds)).getQuery();
        return query.fetchInto(CompletedTask.class);
    }

    public List<Long> listSubjectIds(Long teamId){
        final SelectQuery<Record1<Long>> query = dslContext.select(COMPLETED_TASK.SUBJECT_ID).from(COMPLETED_TASK)
                .where(COMPLETED_TASK.TEAM_ID.eq(teamId)).groupBy(COMPLETED_TASK.SUBJECT_ID).getQuery();
        return query.fetchInto(Long.class);
    }
}
 
