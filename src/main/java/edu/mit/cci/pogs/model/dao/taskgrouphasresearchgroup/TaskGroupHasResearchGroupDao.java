package edu.mit.cci.pogs.model.dao.taskgrouphasresearchgroup;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasResearchGroup;

import java.util.List;

public interface TaskGroupHasResearchGroupDao extends Dao<TaskGroupHasResearchGroup, Long> {

    List<TaskGroupHasResearchGroup> list();
    List<TaskGroupHasResearchGroup> listByTaskGroupId(Long taskGroupId);
    List<TaskGroupHasResearchGroup> listByResearchGroup(Long researchGroupId);
    void delete(TaskGroupHasResearchGroup rghau);

}
