package edu.mit.cci.pogs.model.dao.taskgroup;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroup;
 
public interface TaskGroupDao extends Dao<TaskGroup, Long> {
 
    List<TaskGroup> list();

    List<TaskGroup> listTaskGroupsWithUserGroup(Long id);
}
 
