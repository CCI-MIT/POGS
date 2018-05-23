package edu.mit.cci.pogs.model.dao.taskhasresearchgroup;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasResearchGroup;
 
public interface TaskHasResearchGroupDao extends Dao<TaskHasResearchGroup, Long> {
 
    List<TaskHasResearchGroup> list();
    List<TaskHasResearchGroup> listByTaskId(Long taskId);
    void delete(TaskHasResearchGroup rghau);
}
 
