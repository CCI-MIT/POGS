package edu.mit.cci.pogs.model.dao.task;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
 
public interface TaskDao extends Dao<Task, Long> {
 
    List<Task> list();
}
 
