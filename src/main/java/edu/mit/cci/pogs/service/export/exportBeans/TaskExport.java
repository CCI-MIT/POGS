package edu.mit.cci.pogs.service.export.exportBeans;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;

public class TaskExport extends Task {

    private String taskGroupName;


    private Long taskGroupId;

    private String taskConfigurationName;
    private String taskConfigurationBeforeWorkScriptName;
    private String taskConfigurationAfterWorkScriptName;
    private String taskConfigurationScoreScriptName;
    private String taskConfigurationDictionaryName;

    public TaskExport() {
        super();
    }

    public TaskExport(Task e) {
        super(e);
    }

    public String getTaskGroupName() {
        return taskGroupName;
    }

    public void setTaskGroupName(String taskGroupName) {
        this.taskGroupName = taskGroupName;
    }

    public String getTaskConfigurationName() {
        return taskConfigurationName;
    }

    public void setTaskConfigurationName(String taskConfigurationName) {
        this.taskConfigurationName = taskConfigurationName;
    }

    public String getTaskConfigurationBeforeWorkScriptName() {
        return taskConfigurationBeforeWorkScriptName;
    }

    public void setTaskConfigurationBeforeWorkScriptName(String taskConfigurationBeforeWorkScriptName) {
        this.taskConfigurationBeforeWorkScriptName = taskConfigurationBeforeWorkScriptName;
    }

    public String getTaskConfigurationAfterWorkScriptName() {
        return taskConfigurationAfterWorkScriptName;
    }

    public void setTaskConfigurationAfterWorkScriptName(String taskConfigurationAfterWorkScriptName) {
        this.taskConfigurationAfterWorkScriptName = taskConfigurationAfterWorkScriptName;
    }

    public String getTaskConfigurationScoreScriptName() {
        return taskConfigurationScoreScriptName;
    }

    public void setTaskConfigurationScoreScriptName(String taskConfigurationScoreScriptName) {
        this.taskConfigurationScoreScriptName = taskConfigurationScoreScriptName;
    }

    public String getTaskConfigurationDictionaryName() {
        return taskConfigurationDictionaryName;
    }

    public void setTaskConfigurationDictionaryName(String taskConfigurationDictionaryName) {
        this.taskConfigurationDictionaryName = taskConfigurationDictionaryName;
    }

    public Long getTaskGroupId() {
        return taskGroupId;
    }

    public void setTaskGroupId(Long taskGroupId) {
        this.taskGroupId = taskGroupId;
    }
}
