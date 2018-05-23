package edu.mit.cci.pogs.model.dao.taskgrouphastask;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasTask;
 
public interface TaskGroupHasTaskDao extends Dao<TaskGroupHasTask, Long> {
 
    List<TaskGroupHasTask> list();
    void delete(TaskGroupHasTask rghau);
    List<TaskGroupHasTask> listByTaskGroupId(Long taskGroupId);

}
 
