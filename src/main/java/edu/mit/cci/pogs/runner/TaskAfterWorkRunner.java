package edu.mit.cci.pogs.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;

import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.utils.DateUtils;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskAfterWorkRunner extends TaskRelatedScriptRunner implements Runnable {



    private static final Logger _log = LoggerFactory.getLogger(TaskBeforeWorkRunner.class);

    @Override
    public void run() {
        Long timeBeforeStarts = taskWrapper.getTaskEndTimestamp() - DateUtils.now();
        taskPlugin =  TaskPlugin.getTaskPlugin(taskWrapper.getTaskPluginType());
        taskConfiguration = taskService.getTaskConfiguration(taskWrapper.getId());
        try {
            if (timeBeforeStarts > 0) {
                System.out.println(" Time until task after work for Task id: " + taskWrapper.getId() + " is done: " + timeBeforeStarts);
                Thread.sleep(timeBeforeStarts);
            }
            System.out.println(" Task after work is starting for Task id: " + taskWrapper.getId());
            for (CompletedTask ct : taskWrapper.getCompletedTasks()) {
                if(taskConfiguration.getAfterWorkScriptId()==null) {
                    this.runScript(this.taskPlugin.getTaskAfterWorkJsContent(), ct);
                } else {
                    ExecutableScript es = executableScriptDao.get(taskConfiguration.getAfterWorkScriptId());
                    this.runScript(es.getScriptContent(),ct);
                }
            }

        } catch (InterruptedException ie) {
            _log.info("Stopping scoring script for task : " + taskWrapper.getId());
        }

    }


    @Override
    public void retrieveScriptVariables() {

        retreiveSubjectAttributesToAdd();
        retrieveCompletedTaskAttributesToAdd();
        retrieveEventLogsToAdd();
    }

    @Override
    public void handleScriptFailure(ScriptException se) {
        _log.error("After work script execution error for : " + taskPlugin.getTaskPluginName() + " - " + se.getMessage());
    }

}
