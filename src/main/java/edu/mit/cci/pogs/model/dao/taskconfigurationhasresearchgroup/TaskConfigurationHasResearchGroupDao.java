package edu.mit.cci.pogs.model.dao.taskconfigurationhasresearchgroup;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfigurationHasResearchGroup;

import java.util.List;

public interface TaskConfigurationHasResearchGroupDao extends Dao<TaskConfigurationHasResearchGroup, Long> {

    List<TaskConfigurationHasResearchGroup> list();
    List<TaskConfigurationHasResearchGroup> listByTaskConfigurationId(Long taskConfigurationId);
    List<TaskConfigurationHasResearchGroup> listByResearchGroup(Long researchGroupId);
    void delete(TaskConfigurationHasResearchGroup rghau);

}
