package edu.mit.cci.pogs.runner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptException;

import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskSnapshotExportRunner extends TaskRelatedScriptRunner implements Runnable {

    private List<String> exportLines;

    private String headerColumns;

    private static final Logger _log = LoggerFactory.getLogger(TaskBeforeWorkRunner.class);

    @Override
    public void run() {
        taskPlugin = TaskPlugin.getTaskPlugin(taskWrapper.getTaskPluginType());
        taskConfiguration = taskService.getTaskConfiguration(taskWrapper.getId());

            //if added a field in the future to override the default export
            //if (taskConfiguration.getAfterWorkScriptId() == null) {
            this.runScript(this.taskPlugin.getTaskExportJsContent(), this.getCompletedTask());
                /*} else {
                    ExecutableScript es = executableScriptDao.get(taskConfiguration.getAfterWorkScriptId());
                    this.runScript(es.getScriptContent(), ct);
                }*/

    }


    @Override
    public void retrieveScriptVariables() {

        String exportLinesJson = (String) this.getEngine()
                .getBindings(ScriptContext.ENGINE_SCOPE).get("exportRecordLines");

        JSONArray object = new JSONArray(exportLinesJson);
        exportLines = new ArrayList<>();
        if(object!=null) {
            for (int i = 0; i < object.length(); i++) {
                exportLines.add(object.getString(i));
            }
        }

        String headerColumns = (String) this.getEngine()
                .getBindings(ScriptContext.ENGINE_SCOPE).get("headerColumns");
        if(headerColumns!=null) {
            this.headerColumns = headerColumns;
        }

    }

    @Override
    public void handleScriptFailure(ScriptException se) {
        _log.error("After work script execution error for : " + taskPlugin.getTaskPluginName() + " - " + se.getMessage());
    }

    public List<String> getExportLines() {
        return exportLines;
    }

    public String getHeaderColumns() {
        return headerColumns;
    }
}