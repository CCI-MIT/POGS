package edu.mit.cci.pogs.model.dao.task.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.records.TaskRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.RESEARCH_GROUP_HAS_AUTH_USER;
import static edu.mit.cci.pogs.model.jooq.Tables.TASK;
import static edu.mit.cci.pogs.model.jooq.Tables.TASK_HAS_RESEARCH_GROUP;

@Repository
public class TaskDaoImpl extends AbstractDao<Task, Long, TaskRecord> implements TaskDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public TaskDaoImpl(DSLContext dslContext) {
        super(dslContext, TASK, TASK.ID, Task.class);
        this.dslContext = dslContext;
    }
 
    public List<Task> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK).getQuery();
 
        return query.fetchInto(Task.class);
    }

    @Override
    public List<Task> listTasksWithUserGroup(Long userId) {
        final SelectQuery<Record> query = dslContext.select(TASK.fields())
                .from(TASK)
                .join(TASK_HAS_RESEARCH_GROUP).on(TASK_HAS_RESEARCH_GROUP.TASK_ID.eq(TASK.ID))
                .join(RESEARCH_GROUP_HAS_AUTH_USER).on(RESEARCH_GROUP_HAS_AUTH_USER
                        .RESEARCH_GROUP_ID.eq(TASK_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID))
                .where(RESEARCH_GROUP_HAS_AUTH_USER.AUTH_USER_ID.eq(userId))
                .getQuery();

        return query.fetchInto(Task.class);
    }
}
 
