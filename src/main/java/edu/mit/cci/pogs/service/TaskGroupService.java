package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.taskgrouphasresearchgroup.TaskGroupHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasResearchGroup;
import edu.mit.cci.pogs.service.base.ServiceBase;
import edu.mit.cci.pogs.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.mit.cci.pogs.model.dao.taskgroup.TaskGroupDao;
import edu.mit.cci.pogs.model.dao.taskgrouphastask.TaskGroupHasTaskDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasTask;
import edu.mit.cci.pogs.view.taskgroup.bean.TaskGroupBean;

@Service
public class TaskGroupService extends ServiceBase {


    private final TaskGroupDao taskGroupDao;

    private final TaskGroupHasResearchGroupDao taskGroupHasResearchGroupDao;

    private final TaskGroupHasTaskDao taskGroupHasTaskDao;

    @Autowired
    public TaskGroupService(TaskGroupDao taskGroupDao, TaskGroupHasTaskDao taskGroupHasTaskDao, TaskGroupHasResearchGroupDao taskGroupHasResearchGroupDao) {
        this.taskGroupHasTaskDao = taskGroupHasTaskDao;
        this.taskGroupDao = taskGroupDao;
        this.taskGroupHasResearchGroupDao = taskGroupHasResearchGroupDao;
    }

    public List<TaskGroupHasResearchGroup> listTaskGroupHasResearchGroupByTaskGroup(Long taskGroupId) {
        return taskGroupHasResearchGroupDao.listByTaskGroupId(taskGroupId);
    }

    public TaskGroup createOrUpdate(TaskGroupBean taskGroupBean) {

        TaskGroup tg = new TaskGroup();

        ObjectUtils.Copy(tg, taskGroupBean);

        if (tg.getId() == null) {
            tg = taskGroupDao.create(tg);
            taskGroupBean.setId(tg.getId());
            createOrUpdateUserGroups(taskGroupBean);
        } else {
            taskGroupDao.update(tg);
            createOrUpdateUserGroups(taskGroupBean);
        }
        createOrUpdateTaskGroupHasTask(taskGroupBean);
        return tg;
    }

    private void createOrUpdateUserGroups(TaskGroupBean taskGroupBean) {
        if (taskGroupBean.getResearchGroupRelationshipBean() == null && taskGroupBean.getResearchGroupRelationshipBean().getSelectedValues() == null) {
            return;
        }

        List<Long> toCreate = new ArrayList<>();
        List<Long> toDelete = new ArrayList<>();
        List<TaskGroupHasResearchGroup> currentlySelected = listTaskGroupHasResearchGroupByTaskGroup(taskGroupBean.getId());

        List<Long> currentResearchGroups = currentlySelected
                .stream()
                .map(TaskGroupHasResearchGroup::getResearchGroupId)
                .collect(Collectors.toList());

        String[] newSelectedValues = taskGroupBean.getResearchGroupRelationshipBean().getSelectedValues();

        UpdateResearchGroups(toCreate, toDelete, currentResearchGroups, newSelectedValues);

        for (Long toCre : toCreate) {
            TaskGroupHasResearchGroup rghau = new TaskGroupHasResearchGroup();
            rghau.setTaskGroupId(taskGroupBean.getId());
            rghau.setResearchGroupId(toCre);
            taskGroupHasResearchGroupDao.create(rghau);
        }
        for (Long toDel : toDelete) {

            TaskGroupHasResearchGroup rghau = currentlySelected
                    .stream()
                    .filter(a -> (a.getTaskGroupId() == taskGroupBean.getId() && a.getResearchGroupId() == toDel))
                    .findFirst().get();

            taskGroupHasResearchGroupDao.delete(rghau);
        }

    }

    private void createOrUpdateTaskGroupHasTask(TaskGroupBean studyBean) {

        if (studyBean.getSelectedTasks() == null && studyBean.getSelectedTasks() == null) {
            return;
        }
        List<TaskGroupHasTask> toCreate = new ArrayList<>();
        List<TaskGroupHasTask> toUpdate = new ArrayList<>();
        List<TaskGroupHasTask> toDelete = new ArrayList<>();
        List<TaskGroupHasTask> currentlySelected = listTaskGroupHasTaskByTaskGroup(studyBean.getId());


        for (TaskGroupHasTask rghau : currentlySelected) {
            boolean foundRGH = false;
            for (Long taskId : studyBean.getSelectedTasks()) {
                if (rghau.getTaskId().longValue() == new Long(taskId).longValue()) {
                    foundRGH = true;
                }
            }
            if (!foundRGH) {
                toDelete.add(rghau);
            }

        }
        int counter = 0;
        for (Long taskId : studyBean.getSelectedTasks()) {

            boolean selectedAlreadyIn = false;
            for (TaskGroupHasTask rghau : currentlySelected) {
                if (rghau.getTaskId().longValue() == new Long(taskId).longValue()) {
                    selectedAlreadyIn = true;

                    if (rghau.getOrder().intValue() != new Integer(counter).intValue()) {
                        rghau.setOrder(counter);
                        toUpdate.add(rghau);
                    }
                }
            }
            if (!selectedAlreadyIn) {
                TaskGroupHasTask rghau = new TaskGroupHasTask();
                rghau.setTaskGroupId(studyBean.getId());
                rghau.setTaskId(new Long(taskId));
                rghau.setOrder(counter);
                toCreate.add(rghau);
            }
            counter++;
        }
        for (TaskGroupHasTask toCre : toCreate) {
            taskGroupHasTaskDao.create(toCre);
        }
        for (TaskGroupHasTask toUp : toUpdate) {
            taskGroupHasTaskDao.update(toUp);
        }
        for (TaskGroupHasTask toDel : toDelete) {
            taskGroupHasTaskDao.delete(toDel);
        }


    }

    public List<TaskGroupHasTask> listTaskGroupHasTaskByTaskGroup(Long taskGroupId) {
        return taskGroupHasTaskDao.listByTaskGroupId(taskGroupId);
    }
}
