package edu.mit.cci.pogs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.taskgroup.TaskGroupDao;
import edu.mit.cci.pogs.model.dao.taskgrouphastask.TaskGroupHasTaskDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasTask;
import edu.mit.cci.pogs.view.taskgroup.bean.TaskGroupBean;

@Service
public class TaskGroupService {


    private final TaskGroupDao taskGroupDao;


    private final TaskGroupHasTaskDao taskGroupHasTaskDao;

    @Autowired
    public TaskGroupService(TaskGroupDao taskGroupDao, TaskGroupHasTaskDao taskGroupHasTaskDao) {
        this.taskGroupHasTaskDao = taskGroupHasTaskDao;
        this.taskGroupDao = taskGroupDao;
    }

    public TaskGroup createOrUpdate(TaskGroupBean taskGroupBean) {

        TaskGroup tg = new TaskGroup();
        tg.setId(taskGroupBean.getId());
        tg.setTaskGroupName(taskGroupBean.getTaskGroupName());


        if (tg.getId() == null) {
            tg = taskGroupDao.create(tg);
            taskGroupBean.setId(tg.getId());
        } else {
            taskGroupDao.update(tg);
        }
        createOrUpdateTaskGroupHasTask(taskGroupBean);
        return tg;
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
