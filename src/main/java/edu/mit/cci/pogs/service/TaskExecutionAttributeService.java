package edu.mit.cci.pogs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.taskexecutionattribute.TaskExecutionAttributeDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.view.taskplugin.beans.TaskPluginConfigBean;

@Service
public class TaskExecutionAttributeService {

    @Autowired
    private TaskExecutionAttributeDao taskExecutionAttributeDao;

    public void createOrUpdateTaskExecutionAttribute(TaskPluginConfigBean taskPluginConfigBean) {
        if (taskPluginConfigBean.getAttributes() == null) {
            return;
        }
        List<TaskExecutionAttribute> currentlySelected = taskExecutionAttributeDao.listByTaskConfigurationId(taskPluginConfigBean.getId());
        List<TaskExecutionAttribute> toBeDeleted = new ArrayList<>(currentlySelected);

        for (TaskExecutionAttribute tea : taskPluginConfigBean.getAttributes()) {
            if (tea.getId() != null) {
                taskExecutionAttributeDao.update(tea);
                for (TaskExecutionAttribute twa : currentlySelected) {
                    if (twa.getId().longValue() == (tea.getId().longValue())) {
                        toBeDeleted.remove(twa);
                    }
                }
            } else {
                tea.setTaskConfigurationId(taskPluginConfigBean.getId());
                taskExecutionAttributeDao.create(tea);
            }
        }
        for (TaskExecutionAttribute twa : toBeDeleted) {
            taskExecutionAttributeDao.delete(twa);
        }

    }

}
