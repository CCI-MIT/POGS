package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.service.base.ServiceBase;
import edu.mit.cci.pogs.utils.ObjectUtils;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskconfiguration.TaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskhasresearchgroup.TaskHasResearchGroupDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.view.task.bean.TaskBean;

@Service
public class TaskService extends ServiceBase {

    private final TaskDao taskDao;
    private final TaskHasResearchGroupDao taskHasResearchGroupDao;
    private final TaskHasTaskConfigurationDao taskHasTaskConfigurationDao;
    private final TaskConfigurationDao taskConfigurationDao;


    @Autowired
    public TaskService(TaskDao taskDao, TaskHasResearchGroupDao taskHasResearchGroupDao,
                       TaskHasTaskConfigurationDao taskHasTaskConfigurationDao,
                       TaskConfigurationDao taskConfigurationDao) {
        this.taskHasResearchGroupDao = taskHasResearchGroupDao;
        this.taskDao = taskDao;
        this.taskHasTaskConfigurationDao = taskHasTaskConfigurationDao;
        this.taskConfigurationDao = taskConfigurationDao;
    }

    public List<TaskHasResearchGroup> listTaskHasResearchGroupByTaskId(Long taskId) {

        return taskHasResearchGroupDao.listByTaskId(taskId);
    }


    private static void updateVideoPrimerUrl(Task tk) {
        if (tk.getPrimerPageEnabled() && tk.getPrimerVideoAutoplayMute()) {
            String primerContent = tk.getPrimerText();
            Pattern p = Pattern.compile("(?:https?://)?(?:www.)?(?:youtube.com|youtu.be|vimeo.com)/(?:watch\\?v=)?([^\\s|^\"])+");
            Matcher urlMatcher = p.matcher(primerContent);
            if (urlMatcher.find()) {
                String url = urlMatcher.group(0);
                String oldUrl = url;

                if (!url.contains("autoplay=1")) {
                    if (!url.contains("?")) {
                        url += "?autoplay=1&mute=1";
                    } else {
                        url += "&autoplay=1&mute=1";
                    }
                    tk.setPrimerText(primerContent.replace(oldUrl, url));
                }
            }


        }
    }

    public TaskBean createOrUpdate(TaskBean value) {
        Task tk = new Task();

        ObjectUtils.Copy(tk, value);

        updateVideoPrimerUrl(tk);
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
        if (currentConfig != null) {
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

        List<Long> toCreate = new ArrayList<>();
        List<Long> toDelete = new ArrayList<>();
        List<TaskHasResearchGroup> currentlySelected = listTaskHasResearchGroupByTaskId(taskBean.getId());

        List<Long> currentResearchGroups = currentlySelected
                .stream()
                .map(TaskHasResearchGroup::getResearchGroupId)
                .collect(Collectors.toList());

        String[] newSelectedValues = taskBean.getResearchGroupRelationshipBean().getSelectedValues();

        UpdateResearchGroups(toCreate, toDelete, currentResearchGroups, newSelectedValues);

        for (Long toCre : toCreate) {
            TaskHasResearchGroup rghau = new TaskHasResearchGroup();
            rghau.setTaskId(taskBean.getId());
            rghau.setResearchGroupId(toCre);
            taskHasResearchGroupDao.create(rghau);
        }
        for (Long toDel : toDelete) {

            TaskHasResearchGroup rghau = currentlySelected
                    .stream()
                    .filter(a -> (a.getTaskId() == taskBean.getId() && a.getResearchGroupId() == toDel))
                    .findFirst().get();

            taskHasResearchGroupDao.delete(rghau);
        }

    }
    public TaskConfiguration getTaskConfiguration(Long taskId) {

        TaskHasTaskConfiguration configuration = taskHasTaskConfigurationDao
                .getByTaskId(taskId);
        return taskConfigurationDao.get(configuration.getTaskConfigurationId());
    }

    public JSONArray getFakeJsonTaskList() {
        List<TaskWrapper> fakeTaskList = new ArrayList<>();
        TaskWrapper tw = new TaskWrapper();
        tw.setTaskName("Task name");
        tw.setId(01l);
        fakeTaskList.add(tw);

        tw = new TaskWrapper();
        tw.setTaskName("Task name 2");
        tw.setId(02l);
        fakeTaskList.add(tw);
        return getJsonTaskList(fakeTaskList);
    }

    public JSONArray getJsonTaskList(List<TaskWrapper> taskList) {
        JSONArray ja = new JSONArray();
        for (TaskWrapper tw : taskList) {
            JSONObject jo = new JSONObject();
            jo.put("taskName", tw.getTaskName());
            jo.put("id", tw.getId());
            ja.add(jo);
        }
        return ja;
    }
}
