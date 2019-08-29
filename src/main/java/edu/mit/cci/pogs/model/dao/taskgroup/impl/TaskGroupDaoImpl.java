package edu.mit.cci.pogs.model.dao.taskgroup.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.taskgroup.TaskGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.records.TaskGroupRecord;

import static edu.mit.cci.pogs.model.jooq.tables.ResearchGroupHasAuthUser.RESEARCH_GROUP_HAS_AUTH_USER;
import static edu.mit.cci.pogs.model.jooq.tables.TaskGroup.TASK_GROUP;
import static edu.mit.cci.pogs.model.jooq.tables.TaskGroupHasResearchGroup.TASK_GROUP_HAS_RESEARCH_GROUP;


@Repository
public class TaskGroupDaoImpl extends AbstractDao<TaskGroup, Long, TaskGroupRecord> implements TaskGroupDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public TaskGroupDaoImpl(DSLContext dslContext) {
        super(dslContext, TASK_GROUP, TASK_GROUP.ID, TaskGroup.class);
        this.dslContext = dslContext;
    }
 
    public List<TaskGroup> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_GROUP).getQuery();
 
        return query.fetchInto(TaskGroup.class);
    }

    @Override
    public List<TaskGroup> listTaskGroupsWithUserGroup(Long id) {
        final SelectQuery<Record> query = dslContext.selectDistinct(TASK_GROUP.fields())
                .from(TASK_GROUP)
                .join(TASK_GROUP_HAS_RESEARCH_GROUP).on(TASK_GROUP_HAS_RESEARCH_GROUP.TASK_GROUP_ID.eq(TASK_GROUP.ID))
                .join(RESEARCH_GROUP_HAS_AUTH_USER).on(RESEARCH_GROUP_HAS_AUTH_USER.RESEARCH_GROUP_ID.eq(TASK_GROUP_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID))
                .where(RESEARCH_GROUP_HAS_AUTH_USER.AUTH_USER_ID.eq(id))
                .orderBy(TASK_GROUP.ID)
                .getQuery();

        return query.fetchInto(TaskGroup.class);
    }

}
 
