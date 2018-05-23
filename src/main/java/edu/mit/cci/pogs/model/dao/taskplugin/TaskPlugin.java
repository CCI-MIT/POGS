package edu.mit.cci.pogs.model.dao.taskplugin;

public class TaskPlugin {

    private String taskPluginName;

    private boolean isExternal;

    public TaskPlugin(String taskPluginName, boolean isExternal){
        this.taskPluginName = taskPluginName;
        this.isExternal = isExternal;
    }


    public String getTaskPluginName() {
        return taskPluginName;
    }

    public void setTaskPluginName(String taskPluginName) {
        this.taskPluginName = taskPluginName;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }
}
