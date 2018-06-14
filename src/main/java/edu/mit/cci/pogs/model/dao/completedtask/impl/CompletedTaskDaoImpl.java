package edu.mit.cci.pogs.model.dao.completedtask.impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.records.CompletedTaskRecord;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.COMPLETED_TASK;

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

}
 
