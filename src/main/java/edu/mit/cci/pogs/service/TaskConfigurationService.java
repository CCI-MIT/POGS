package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.taskconfiguration.TaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskconfigurationhasresearchgroup.TaskConfigurationHasResearchGroupDao;
import edu.mit.cci.pogs.model.dao.taskexecutionattribute.TaskExecutionAttributeDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfigurationHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.service.base.ServiceBase;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.utils.ObjectUtils;
import edu.mit.cci.pogs.view.taskplugin.beans.TaskPluginConfigBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskConfigurationService extends ServiceBase {

    @Autowired
    private TaskExecutionAttributeDao taskExecutionAttributeDao;

    @Autowired
    private TaskHasTaskConfigurationDao taskHasTaskConfigurationDao;

    @Autowired
    private TaskConfigurationDao taskConfigurationDao;

    @Autowired
    private final TaskConfigurationHasResearchGroupDao taskConfigurationHasResearchGroupDao;

    public TaskConfigurationService(TaskConfigurationHasResearchGroupDao taskConfigurationHasResearchGroupDao) {
        this.taskConfigurationHasResearchGroupDao = taskConfigurationHasResearchGroupDao;
    }


    public List<TaskConfigurationHasResearchGroup> listTaskConfigurationyHasResearchGroupByTaskConfigurationId(Long taskConfigurationId) {
        return this.taskConfigurationHasResearchGroupDao.listByTaskConfigurationId(taskConfigurationId);
    }

    private void createOrUpdateUserGroups(TaskPluginConfigBean taskPluginConfigurationBean) {
        if (taskPluginConfigurationBean.getResearchGroupRelationshipBean() == null && taskPluginConfigurationBean.getResearchGroupRelationshipBean().getSelectedValues() == null) {
            return;
        }

        List<Long> toCreate = new ArrayList<>();
        List<Long> toDelete = new ArrayList<>();
        List<TaskConfigurationHasResearchGroup> currentlySelected = listTaskConfigurationyHasResearchGroupByTaskConfigurationId(taskPluginConfigurationBean.getId());

        List<Long> currentResearchGroups = currentlySelected
                .stream()
                .map(TaskConfigurationHasResearchGroup::getResearchGroupId)
                .collect(Collectors.toList());

        String[] newSelectedValues = taskPluginConfigurationBean.getResearchGroupRelationshipBean().getSelectedValues();

        UpdateResearchGroups(toCreate, toDelete, currentResearchGroups, newSelectedValues);

        for (Long toCre : toCreate) {
            TaskConfigurationHasResearchGroup rghau = new TaskConfigurationHasResearchGroup();
            rghau.setTaskConfigurationId(taskPluginConfigurationBean.getId());
            rghau.setResearchGroupId(toCre);
            taskConfigurationHasResearchGroupDao.create(rghau);
        }
        for (Long toDel : toDelete) {

            TaskConfigurationHasResearchGroup rghau = currentlySelected
                    .stream()
                    .filter(a -> (a.getTaskConfigurationId() == taskPluginConfigurationBean.getId() && a.getResearchGroupId() == toDel))
                    .findFirst().get();

            taskConfigurationHasResearchGroupDao.delete(rghau);
        }

    }

    public TaskConfiguration createOrUpdate(TaskPluginConfigBean taskPluginConfigurationBean) {

        TaskConfiguration taskPluginConfiguration = new TaskConfiguration();

        ObjectUtils.Copy(taskPluginConfiguration, taskPluginConfigurationBean);

        if (taskPluginConfiguration.getId() == null) {
            taskPluginConfiguration = taskConfigurationDao.create(taskPluginConfiguration);
            taskPluginConfigurationBean.setId(taskPluginConfiguration.getId());
            createOrUpdateUserGroups(taskPluginConfigurationBean);

            if(taskPluginConfigurationBean.getAttributes()!=null) {
                for (TaskExecutionAttribute tea : taskPluginConfigurationBean.getAttributes()) {
                    tea.setId(null);
                }
            }

        } else {
            taskConfigurationDao.update(taskPluginConfiguration);
            createOrUpdateUserGroups(taskPluginConfigurationBean);
        }
        return taskPluginConfiguration;

    }
}
