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

import static edu.mit.cci.pogs.model.jooq.Tables.*;

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

    public TaskConfiguration getByTaskPluginConfigurationName(String taskPluginName) {
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_CONFIGURATION).getQuery();
        query.addConditions(TASK_CONFIGURATION.CONFIGURATION_NAME.eq(taskPluginName));
        Record record =  query.fetchOne();
        if(record == null) {
            return null;
        }else{
            return record.into(TaskConfiguration.class);
        }
    }

    public TaskConfiguration getByTaskPluginConfigurationId(long taskPluginInt) {
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_CONFIGURATION).getQuery();
        query.addConditions(TASK_CONFIGURATION.ID.eq(taskPluginInt));
        Record record =  query.fetchOne();
        if(record == null) {
            return null;
        }else{
            return record.into(TaskConfiguration.class);
        }
    }

    @Override
    public List<TaskConfiguration> listTaskConfigurationsByNameWithUserGroup(String taskPluginName, Long userId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_CONFIGURATION)
                .join(TASK_CONFIGURATION_HAS_RESEARCH_GROUP).on(TASK_CONFIGURATION_HAS_RESEARCH_GROUP.TASK_CONFIGURATION_ID.eq(TASK_CONFIGURATION.ID))
                .join(RESEARCH_GROUP_HAS_AUTH_USER).on(RESEARCH_GROUP_HAS_AUTH_USER.RESEARCH_GROUP_ID.eq(TASK_CONFIGURATION_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID))
                .where(RESEARCH_GROUP_HAS_AUTH_USER.AUTH_USER_ID.eq(userId))
                .getQuery();

        query.addConditions(TASK_CONFIGURATION.TASK_PLUGIN_NAME.eq(taskPluginName));
        return query.fetchInto(TaskConfiguration.class);
    }




}
 
