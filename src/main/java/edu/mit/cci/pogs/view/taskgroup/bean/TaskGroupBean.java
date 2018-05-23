package edu.mit.cci.pogs.view.taskgroup.bean;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroup;

public class TaskGroupBean {

    private Long id;
    private String taskGroupName;
    private List<Long> selectedTasks;


    public TaskGroupBean() {

    }

    public TaskGroupBean(TaskGroup tg) {
        this.id = tg.getId();
        this.taskGroupName = tg.getTaskGroupName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskGroupName() {
        return taskGroupName;
    }

    public void setTaskGroupName(String taskGroupName) {
        this.taskGroupName = taskGroupName;
    }

    public List<Long> getSelectedTasks() {
        return selectedTasks;
    }

    public void setSelectedTasks(List<Long> selectedTasks) {
        this.selectedTasks = selectedTasks;
    }
}
