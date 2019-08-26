package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.taskconfiguration.TaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskconfigurationhasresearchgroup.TaskConfigurationHasResearchGroupDao;
import edu.mit.cci.pogs.model.dao.taskexecutionattribute.TaskExecutionAttributeDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfigurationHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.view.taskplugin.beans.TaskPluginConfigBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskConfigurationService {

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
        List<TaskConfigurationHasResearchGroup> toCreate = new ArrayList<>();
        List<TaskConfigurationHasResearchGroup> toDelete = new ArrayList<>();
        List<TaskConfigurationHasResearchGroup> currentlySelected = listTaskConfigurationyHasResearchGroupByTaskConfigurationId(taskPluginConfigurationBean.getId());

        for (TaskConfigurationHasResearchGroup rghau : currentlySelected) {
            boolean foundRGH = false;
            for (String researchGroupId : taskPluginConfigurationBean.getResearchGroupRelationshipBean().getSelectedValues()) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    foundRGH = true;
                }
            }
            if (!foundRGH) {
                toDelete.add(rghau);
            }

        }


        for (String researchGroupId : taskPluginConfigurationBean.getResearchGroupRelationshipBean().getSelectedValues()) {

            boolean selectedAlreadyIn = false;
            for (TaskConfigurationHasResearchGroup rghau : currentlySelected) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    selectedAlreadyIn = true;
                }
            }
            if (!selectedAlreadyIn) {
                TaskConfigurationHasResearchGroup rghau = new TaskConfigurationHasResearchGroup();
                rghau.setTaskConfigurationId(taskPluginConfigurationBean.getId());
                rghau.setResearchGroupId(new Long(researchGroupId));
                toCreate.add(rghau);
            }

        }
        for (TaskConfigurationHasResearchGroup toCre : toCreate) {
            taskConfigurationHasResearchGroupDao.create(toCre);
        }
        for (TaskConfigurationHasResearchGroup toDel : toDelete) {
            taskConfigurationHasResearchGroupDao.delete(toDel);
        }

    }

    public TaskConfiguration createOrUpdate(TaskPluginConfigBean taskPluginConfigurationBean) {

        TaskConfiguration taskPluginConfiguration = new TaskConfiguration();
        taskPluginConfiguration.setId(taskPluginConfigurationBean.getId());
        taskPluginConfiguration.setConfigurationName(taskPluginConfigurationBean.getConfigurationName());
        taskPluginConfiguration.setScoreScriptId(taskPluginConfigurationBean.getScoreScriptId());
        taskPluginConfiguration.setAfterWorkScriptId(taskPluginConfigurationBean.getAfterWorkScriptId());
        taskPluginConfiguration.setBeforeWorkScriptId(taskPluginConfigurationBean.getBeforeWorkScriptId());
        taskPluginConfiguration.setDictionaryId(taskPluginConfigurationBean.getDictionaryId());
        taskPluginConfiguration.setTaskPluginName(taskPluginConfigurationBean.getTaskPluginName());

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
