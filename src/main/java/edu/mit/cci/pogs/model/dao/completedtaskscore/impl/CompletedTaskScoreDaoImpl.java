package edu.mit.cci.pogs.model.dao.completedtaskscore.impl;

import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.completedtaskscore.CompletedTaskScoreDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskScore;
import edu.mit.cci.pogs.model.jooq.tables.records.CompletedTaskScoreRecord;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.COMPLETED_TASK_SCORE;

@Repository
public class CompletedTaskScoreDaoImpl extends AbstractDao<CompletedTaskScore, Long, CompletedTaskScoreRecord> implements CompletedTaskScoreDao {

    private final DSLContext dslContext;

    @Autowired
    public CompletedTaskScoreDaoImpl(DSLContext dslContext) {
        super(dslContext, COMPLETED_TASK_SCORE, COMPLETED_TASK_SCORE.ID, CompletedTaskScore.class);
        this.dslContext = dslContext;
    }

    public CompletedTaskScore getByCompletedTaskId(Long completedTaskScoreId) {

            final SelectQuery<Record> query = dslContext.select()
                    .from(COMPLETED_TASK_SCORE).getQuery();

            query.addConditions(COMPLETED_TASK_SCORE.COMPLETED_TASK_ID.eq(completedTaskScoreId));


            Record record = query.fetchOne();
            if (record == null) {
                return null;
            } else {
                return record.into(CompletedTaskScore.class);
            }

    }
    public void deleteByCompletedTaskId(Long completedTaskId) {
        dslContext.delete(COMPLETED_TASK_SCORE)
                .where(COMPLETED_TASK_SCORE.COMPLETED_TASK_ID.eq(completedTaskId))
                .execute();
    }

    public List<CompletedTaskScore> listByCompletedTasksIds(List<Long> completedTaskIds){
        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK_SCORE).where(COMPLETED_TASK_SCORE.COMPLETED_TASK_ID.in(completedTaskIds)).getQuery();
        return query.fetchInto(CompletedTaskScore.class);
    }

    public Double getScore(Long completedTaskID)
    {
        final SelectConditionStep<Record1<Double>> query = dslContext.select(COMPLETED_TASK_SCORE.TOTAL_SCORE).from(COMPLETED_TASK_SCORE).where(COMPLETED_TASK_SCORE.COMPLETED_TASK_ID.eq(completedTaskID));
        return query.fetchOne(0, Double.class);
    }
}
