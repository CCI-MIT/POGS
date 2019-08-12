package edu.mit.cci.pogs.model.dao.taskgrouphasresearchgroup.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;


import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.taskgrouphasresearchgroup.TaskGroupHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.records.TaskGroupHasResearchGroupRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.TASK_GROUP_HAS_RESEARCH_GROUP;

@Repository
public class TaskGroupHasResearchGroupDaoImpl extends AbstractDao<TaskGroupHasResearchGroup, Long, TaskGroupHasResearchGroupRecord> implements TaskGroupHasResearchGroupDao {

    private final DSLContext dslContext;

    @Autowired
    public TaskGroupHasResearchGroupDaoImpl(DSLContext dslContext) {
        super(dslContext, TASK_GROUP_HAS_RESEARCH_GROUP, TASK_GROUP_HAS_RESEARCH_GROUP.ID, TaskGroupHasResearchGroup.class);
        this.dslContext = dslContext;
    }

    public List<TaskGroupHasResearchGroup> list(){

        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_GROUP_HAS_RESEARCH_GROUP).getQuery();

        return query.fetchInto(TaskGroupHasResearchGroup.class);
    }

    public List<TaskGroupHasResearchGroup> listByTaskGroupId(Long taskGroupId){

        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_GROUP_HAS_RESEARCH_GROUP).getQuery();
        query.addConditions(TASK_GROUP_HAS_RESEARCH_GROUP.TASK_GROUP_ID.eq(taskGroupId));
        return query.fetchInto(TaskGroupHasResearchGroup.class);
    }

    public List<TaskGroupHasResearchGroup> listByResearchGroup(Long researchGroupId){

        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_GROUP_HAS_RESEARCH_GROUP).getQuery();
        query.addConditions(TASK_GROUP_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID.eq(researchGroupId));
        return query.fetchInto(TaskGroupHasResearchGroup.class);
    }

    public void delete(TaskGroupHasResearchGroup rghau) {
        dslContext.delete(TASK_GROUP_HAS_RESEARCH_GROUP)
                .where(TASK_GROUP_HAS_RESEARCH_GROUP.ID.eq(rghau.getId()))
                .execute();

    }

}
