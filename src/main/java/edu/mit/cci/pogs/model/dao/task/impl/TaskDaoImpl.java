package edu.mit.cci.pogs.model.dao.task.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.records.TaskRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
import static edu.mit.cci.pogs.model.jooq.Tables.TASK;
 
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
 
}
 
