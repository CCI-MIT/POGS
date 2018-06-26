package edu.mit.cci.pogs.model.dao.taskhastaskconfiguration;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
 
public interface TaskHasTaskConfigurationDao extends Dao<TaskHasTaskConfiguration, Long> {
 
    List<TaskHasTaskConfiguration> list();

    TaskHasTaskConfiguration getByTaskIdRoundId(Long taskId, Long roundId);
}
 
