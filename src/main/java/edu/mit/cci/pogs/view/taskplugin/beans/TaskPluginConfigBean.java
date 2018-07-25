package edu.mit.cci.pogs.view.taskplugin.beans;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;

public class TaskPluginConfigBean extends TaskConfiguration {
    public TaskPluginConfigBean() {

    }

    public TaskPluginConfigBean(TaskConfiguration tc) {
        super(tc);

    }

    private List<TaskExecutionAttribute> attributes;

    public List<TaskExecutionAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<TaskExecutionAttribute> attributes) {
        this.attributes = attributes;
    }
    public String getJsonAttributes(){
        JSONArray ja = new JSONArray();
        if(attributes!=null) {
            for (TaskExecutionAttribute tea : attributes) {
                JSONObject jo = new JSONObject();
                jo.put("id", tea.getId());
                jo.put("attributeName", tea.getAttributeName());
                jo.put("stringValue", tea.getStringValue());
                jo.put("integerValue", tea.getIntegerValue());
                jo.put("double", tea.getDoubleValue());
                jo.put("taskConfigurationId", tea.getTaskConfigurationId());
                ja.add(jo);
            }
        }
        return ja.toString();
    }
}
