package edu.mit.cci.pogs.model.dao.taskconfigurationhasresearchgroup.impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.taskconfigurationhasresearchgroup.TaskConfigurationHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfigurationHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.records.TaskConfigurationHasResearchGroupRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static edu.mit.cci.pogs.model.jooq.Tables.TASK_CONFIGURATION_HAS_RESEARCH_GROUP;

import java.util.List;

@Repository
public class TaskConfigurationHasResearchGroupDaoImpl extends AbstractDao<TaskConfigurationHasResearchGroup, Long, TaskConfigurationHasResearchGroupRecord> implements TaskConfigurationHasResearchGroupDao {


    private final DSLContext dslContext;

    @Autowired
    public TaskConfigurationHasResearchGroupDaoImpl(DSLContext dslContext) {
        super(dslContext, TASK_CONFIGURATION_HAS_RESEARCH_GROUP, TASK_CONFIGURATION_HAS_RESEARCH_GROUP.ID, TaskConfigurationHasResearchGroup.class);
        this.dslContext = dslContext;
    }

    @Override
    public List<TaskConfigurationHasResearchGroup> list() {
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_CONFIGURATION_HAS_RESEARCH_GROUP).getQuery();

        return query.fetchInto(TaskConfigurationHasResearchGroup.class);
    }

    @Override
    public List<TaskConfigurationHasResearchGroup> listByTaskConfigurationId(Long taskConfigurationId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_CONFIGURATION_HAS_RESEARCH_GROUP).getQuery();
        query.addConditions(TASK_CONFIGURATION_HAS_RESEARCH_GROUP.TASK_CONFIGURATION_ID.eq(taskConfigurationId));
        return query.fetchInto(TaskConfigurationHasResearchGroup.class);
    }

    @Override
    public List<TaskConfigurationHasResearchGroup> listByResearchGroup(Long researchGroupId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_CONFIGURATION_HAS_RESEARCH_GROUP).getQuery();
        query.addConditions(TASK_CONFIGURATION_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID.eq(researchGroupId));
        return query.fetchInto(TaskConfigurationHasResearchGroup.class);
    }

    @Override
    public void delete(TaskConfigurationHasResearchGroup rghau){
        dslContext.delete(TASK_CONFIGURATION_HAS_RESEARCH_GROUP)
                .where(TASK_CONFIGURATION_HAS_RESEARCH_GROUP.ID.eq(rghau.getId()))
                .execute();
    }
}
