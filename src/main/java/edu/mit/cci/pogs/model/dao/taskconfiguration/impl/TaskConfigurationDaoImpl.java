package edu.mit.cci.pogs.model.dao.taskconfiguration.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.taskconfiguration.TaskConfigurationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.records.TaskConfigurationRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.TASK_CONFIGURATION;

@Repository
public class TaskConfigurationDaoImpl extends AbstractDao<TaskConfiguration, Long, TaskConfigurationRecord> implements TaskConfigurationDao {

    private final DSLContext dslContext;

    @Autowired
    public TaskConfigurationDaoImpl(DSLContext dslContext) {
        super(dslContext, TASK_CONFIGURATION, TASK_CONFIGURATION.ID, TaskConfiguration.class);
        this.dslContext = dslContext;
    }

    public List<TaskConfiguration> list() {

        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_CONFIGURATION).getQuery();

        return query.fetchInto(TaskConfiguration.class);
    }

    public List<TaskConfiguration> listByTaskPluginName(String taskPluginName) {
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_CONFIGURATION).getQuery();
        query.addConditions(TASK_CONFIGURATION.TASK_PLUGIN_NAME.eq(taskPluginName));
        return query.fetchInto(TaskConfiguration.class);
    }


}
 
