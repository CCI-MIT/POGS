package edu.mit.cci.pogs.model.dao.taskgrouphastask.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.taskgrouphastask.TaskGroupHasTaskDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroupHasAuthUser;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasTask;
import edu.mit.cci.pogs.model.jooq.tables.records.TaskGroupHasTaskRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.RESEARCH_GROUP_HAS_AUTH_USER;
import static edu.mit.cci.pogs.model.jooq.Tables.TASK_GROUP_HAS_TASK;
import static edu.mit.cci.pogs.model.jooq.Tables.TASK_HAS_RESEARCH_GROUP;

@Repository
public class TaskGroupHasTaskDaoImpl extends AbstractDao<TaskGroupHasTask, Long, TaskGroupHasTaskRecord> implements TaskGroupHasTaskDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public TaskGroupHasTaskDaoImpl(DSLContext dslContext) {
        super(dslContext, TASK_GROUP_HAS_TASK, TASK_GROUP_HAS_TASK.ID, TaskGroupHasTask.class);
        this.dslContext = dslContext;
    }
 
    public List<TaskGroupHasTask> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_GROUP_HAS_TASK).getQuery();
 
        return query.fetchInto(TaskGroupHasTask.class);
    }



    public List<TaskGroupHasTask> listByTaskGroupId(Long taskGroupId){

        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_GROUP_HAS_TASK).getQuery();

        query.addConditions(TASK_GROUP_HAS_TASK.TASK_GROUP_ID.eq(taskGroupId));
        query.addOrderBy(TASK_GROUP_HAS_TASK.ORDER);
        return query.fetchInto(TaskGroupHasTask.class);
    }
    public void delete(TaskGroupHasTask rghau) {
        dslContext.delete(TASK_GROUP_HAS_TASK)
                .where(TASK_GROUP_HAS_TASK.ID.eq(rghau.getId()))
                .execute();

    }
}
 
