package edu.mit.cci.pogs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskhasresearchgroup.TaskHasResearchGroupDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
import edu.mit.cci.pogs.view.task.bean.TaskBean;

@Service
public class TaskService {

    private final TaskDao taskDao;
    private final TaskHasResearchGroupDao taskHasResearchGroupDao;
    private final TaskHasTaskConfigurationDao taskHasTaskConfigurationDao;


    @Autowired
    public TaskService(TaskDao taskDao, TaskHasResearchGroupDao taskHasResearchGroupDao, TaskHasTaskConfigurationDao taskHasTaskConfigurationDao) {
        this.taskHasResearchGroupDao = taskHasResearchGroupDao;
        this.taskDao = taskDao;
        this.taskHasTaskConfigurationDao = taskHasTaskConfigurationDao;
    }

    public List<TaskHasResearchGroup> listTaskHasResearchGroupByTaskId(Long taskId) {

        return taskHasResearchGroupDao.listByTaskId(taskId);
    }

    public TaskBean createOrUpdate(TaskBean value) {
        Task tk = new Task();

        tk.setId(value.getId());
        tk.setTaskName(value.getTaskName());
        tk.setTaskPluginType(value.getTaskPluginType());
        tk.setSoloTask(value.getSoloTask());
        tk.setInteractionTime(value.getInteractionTime());
        tk.setIntroPageEnabled(value.getIntroPageEnabled());
        tk.setIntroText(value.getIntroText());
        tk.setIntroTime(value.getIntroTime());
        tk.setPrimerPageEnabled(value.getPrimerPageEnabled());
        tk.setPrimerText(value.getPrimerText());
        tk.setPrimerTime(value.getPrimerTime());
        tk.setInteractionWidgetEnabled(value.getInteractionWidgetEnabled());
        tk.setInteractionText(value.getInteractionText());
        tk.setCommunicationType(value.getCommunicationType());
        tk.setCollaborationTodoListEnabled(value.getCollaborationTodoListEnabled());
        tk.setCollaborationFeedbackWidgetEnabled(value.getCollaborationFeedbackWidgetEnabled());
        tk.setCollaborationVotingWidgetEnabled(value.getCollaborationVotingWidgetEnabled());
        tk.setScoringType(value.getScoringType());
        tk.setSubjectCommunicationId(value.getSubjectCommunicationId());
        tk.setChatScriptId(value.getChatScriptId());
        if (tk.getId() == null) {
            tk = taskDao.create(tk);
            value.setId(tk.getId());
            createOrUpdateTaskHasTaskConfiguration(value);

            createOrUpdateUserGroups(value);
        } else {
            taskDao.update(tk);
            createOrUpdateUserGroups(value);
            createOrUpdateTaskHasTaskConfiguration(value);
        }
        return value;
    }

    private void createOrUpdateTaskHasTaskConfiguration(TaskBean value) {
        TaskHasTaskConfiguration currentConfig = taskHasTaskConfigurationDao.getByTaskId(value.getId());
        if(currentConfig!=null) {
            if (currentConfig.getTaskConfigurationId().longValue() == value.getTaskConfigurationId().longValue()) {
                return;
            } else {
                taskHasTaskConfigurationDao.delete(currentConfig);
            }
        }
        TaskHasTaskConfiguration taskHasTaskConfiguration = new TaskHasTaskConfiguration();
        taskHasTaskConfiguration.setTaskId(value.getId());
        taskHasTaskConfiguration.setTaskConfigurationId(value.getTaskConfigurationId());
        taskHasTaskConfigurationDao.create(taskHasTaskConfiguration);

    }

    private void createOrUpdateUserGroups(TaskBean taskBean) {
        if (taskBean.getResearchGroupRelationshipBean() == null && taskBean.getResearchGroupRelationshipBean().getSelectedValues() == null) {
            return;
        }
        List<TaskHasResearchGroup> toCreate = new ArrayList<>();
        List<TaskHasResearchGroup> toDelete = new ArrayList<>();
        List<TaskHasResearchGroup> currentlySelected = listTaskHasResearchGroupByTaskId(taskBean.getId());

        for (TaskHasResearchGroup rghau : currentlySelected) {
            boolean foundRGH = false;
            for (String researchGroupId : taskBean.getResearchGroupRelationshipBean().getSelectedValues()) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    foundRGH = true;
                }
            }
            if (!foundRGH) {
                toDelete.add(rghau);
            }

        }

        for (String researchGroupId : taskBean.getResearchGroupRelationshipBean().getSelectedValues()) {

            boolean selectedAlreadyIn = false;
            for (TaskHasResearchGroup rghau : currentlySelected) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    selectedAlreadyIn = true;
                }
            }
            if (!selectedAlreadyIn) {
                TaskHasResearchGroup rghau = new TaskHasResearchGroup();
                rghau.setTaskId(taskBean.getId());
                rghau.setResearchGroupId(new Long(researchGroupId));
                toCreate.add(rghau);
            }

        }
        for (TaskHasResearchGroup toCre : toCreate) {
            taskHasResearchGroupDao.create(toCre);
        }
        for (TaskHasResearchGroup toDel : toDelete) {
            taskHasResearchGroupDao.delete(toDel);
        }

    }
}
