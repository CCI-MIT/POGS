package edu.mit.cci.pogs.model.dao.taskexecutionattribute.impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.taskexecutionattribute.TaskExecutionAttributeDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.model.jooq.tables.records.TaskExecutionAttributeRecord;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.STUDY_HAS_RESEARCH_GROUP;
import static edu.mit.cci.pogs.model.jooq.Tables.TASK_EXECUTION_ATTRIBUTE;

@Repository
public class TaskExecutionAttributeDaoImpl extends AbstractDao<TaskExecutionAttribute, Long, TaskExecutionAttributeRecord> implements TaskExecutionAttributeDao {

    private final DSLContext dslContext;

    @Autowired
    public TaskExecutionAttributeDaoImpl(DSLContext dslContext) {
        super(dslContext, TASK_EXECUTION_ATTRIBUTE, TASK_EXECUTION_ATTRIBUTE.ID, TaskExecutionAttribute.class);
        this.dslContext = dslContext;
    }

    public List<TaskExecutionAttribute> list() {

        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_EXECUTION_ATTRIBUTE).getQuery();

        return query.fetchInto(TaskExecutionAttribute.class);
    }

    public List<TaskExecutionAttribute> listByTaskConfigurationId(Long taskConfigurationId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_EXECUTION_ATTRIBUTE).getQuery();
        query.addConditions(TASK_EXECUTION_ATTRIBUTE.TASK_CONFIGURATION_ID.eq(taskConfigurationId));

        return query.fetchInto(TaskExecutionAttribute.class);
    }

    public void delete(TaskExecutionAttribute tea) {
        dslContext.delete(TASK_EXECUTION_ATTRIBUTE)
                .where(TASK_EXECUTION_ATTRIBUTE.ID.eq(tea.getId()))
                .execute();

    }

}
 
