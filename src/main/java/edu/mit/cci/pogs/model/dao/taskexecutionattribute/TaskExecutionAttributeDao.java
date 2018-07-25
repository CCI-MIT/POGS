package edu.mit.cci.pogs.model.dao.taskexecutionattribute;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
 
public interface TaskExecutionAttributeDao extends Dao<TaskExecutionAttribute, Long> {
 
    List<TaskExecutionAttribute> list();
    List<TaskExecutionAttribute> listByTaskConfigurationId(Long taskConfigurationId);
    void delete(TaskExecutionAttribute tea);
}
 
