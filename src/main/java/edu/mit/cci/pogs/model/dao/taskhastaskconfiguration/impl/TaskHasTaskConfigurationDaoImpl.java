package edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.records.TaskHasTaskConfigurationRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.TASK_HAS_TASK_CONFIGURATION;

@Repository
public class TaskHasTaskConfigurationDaoImpl extends AbstractDao<TaskHasTaskConfiguration, Long, TaskHasTaskConfigurationRecord> implements TaskHasTaskConfigurationDao {

    private final DSLContext dslContext;

    @Autowired
    public TaskHasTaskConfigurationDaoImpl(DSLContext dslContext) {
        super(dslContext, TASK_HAS_TASK_CONFIGURATION, TASK_HAS_TASK_CONFIGURATION.ID, TaskHasTaskConfiguration.class);
        this.dslContext = dslContext;
    }

    public List<TaskHasTaskConfiguration> list() {

        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_HAS_TASK_CONFIGURATION).getQuery();

        return query.fetchInto(TaskHasTaskConfiguration.class);
    }

    public List<TaskHasTaskConfiguration> listByTaskId(Long taskId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_HAS_TASK_CONFIGURATION).getQuery();

        query.addConditions(TASK_HAS_TASK_CONFIGURATION.TASK_ID.eq(taskId));
        return query.fetchInto(TaskHasTaskConfiguration.class);
    }

    public TaskHasTaskConfiguration getByTaskIdRoundId(Long taskId, Long roundId) {


        //TODO add condition for roundId, after database change
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_HAS_TASK_CONFIGURATION).getQuery();

        query.addConditions(TASK_HAS_TASK_CONFIGURATION.TASK_ID.eq(taskId));

        Record record = query.fetchOne();
        if (record == null) {
            return null;
        } else {
            return record.into(TaskHasTaskConfiguration.class);
        }
    }

}
 
