package edu.mit.cci.pogs.service;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.taskconfiguration.TaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskexecutionattribute.TaskExecutionAttributeDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
import edu.mit.cci.pogs.view.taskplugin.beans.TaskPluginConfigBean;

@Service
public class TaskExecutionAttributeService {

    @Autowired
    private TaskExecutionAttributeDao taskExecutionAttributeDao;

    @Autowired
    private TaskHasTaskConfigurationDao taskHasTaskConfigurationDao;

    @Autowired
    private TaskConfigurationDao taskConfigurationDao;

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

    public JSONArray listExecutionAttributesAsJsonArray(Long taskId){

        TaskHasTaskConfiguration configuration = taskHasTaskConfigurationDao
                .getByTaskId(taskId);
        List<TaskExecutionAttribute> taskExecutionAttributes = taskExecutionAttributeDao
                .listByTaskConfigurationId(configuration.getTaskConfigurationId());

        return attributesToJsonArray(taskExecutionAttributes);
    }

    public JSONArray attributesToJsonArray(List<TaskExecutionAttribute> taskExecutionAttributes) {
        JSONArray configurationArray = new JSONArray();
        for (TaskExecutionAttribute tea : taskExecutionAttributes) {
            JSONObject teaJson = new JSONObject();
            teaJson.put("attributeName", tea.getAttributeName());
            teaJson.put("stringValue", tea.getStringValue());
            teaJson.put("doubleValue", tea.getDoubleValue());
            teaJson.put("integerValue", tea.getIntegerValue());
            configurationArray.put(teaJson);


        }
        return configurationArray;
    }

    public JSONArray listExecutionAttributesFromPluginConfigAsJsonArray(String pluginConfig){
        //get task configurations
        TaskConfiguration tc = taskConfigurationDao.getByTaskPluginConfigurationName(pluginConfig);

        List<TaskExecutionAttribute> taskExecutionAttributes = taskExecutionAttributeDao
                .listByTaskConfigurationId(tc.getId());

        return attributesToJsonArray(taskExecutionAttributes);
    }

    public JSONArray listExecutionAttributesFromPluginConfigAsJsonArray(long pluginConfigId){
        //get task configurations
        TaskConfiguration tc = taskConfigurationDao.getByTaskPluginConfigurationId(pluginConfigId);

        List<TaskExecutionAttribute> taskExecutionAttributes = taskExecutionAttributeDao
                .listByTaskConfigurationId(tc.getId());

        return attributesToJsonArray(taskExecutionAttributes);
    }



}
