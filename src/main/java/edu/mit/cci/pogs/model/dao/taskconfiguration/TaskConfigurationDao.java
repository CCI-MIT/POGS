package edu.mit.cci.pogs.model.dao.taskconfiguration;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
 
public interface TaskConfigurationDao extends Dao<TaskConfiguration, Long> {
 
    List<TaskConfiguration> list();
    List<TaskConfiguration> listByTaskPluginName(String taskPluginName);
}
 
