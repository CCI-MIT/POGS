package edu.mit.cci.pogs.view.taskgroup.bean;

import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasTask;

public class TaskGroupHasTaskBean extends TaskGroupHasTask {

    private String taskName;

    public TaskGroupHasTaskBean(TaskGroupHasTask base){
        super(base);
    }
    public TaskGroupHasTaskBean(){

    }

    public String getTaskName(){
        return taskName;
    }

    public void setTaskName(String taskName){
        this.taskName = taskName;
    }
}
