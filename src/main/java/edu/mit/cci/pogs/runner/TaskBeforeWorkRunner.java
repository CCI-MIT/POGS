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
public class TaskBeforeWorkRunner extends TaskRelatedScriptRunner implements Runnable {



    private int SECONDS_BEFORE_TASK_STARTS = 20 * 1000;

    private static final Logger _log = LoggerFactory.getLogger(TaskBeforeWorkRunner.class);

    @Override
    public void run() {
        Long timeBeforeStarts = (taskWrapper.getTaskStartTimestamp() - SECONDS_BEFORE_TASK_STARTS) - DateUtils.now();
        taskPlugin =  TaskPlugin.getTaskPlugin(taskWrapper.getTaskPluginType());
        taskConfiguration = taskService.getTaskConfiguration(taskWrapper.getId());

        try {
            if (timeBeforeStarts > 0) {
                System.out.println(" Time until Before Task id: "+taskWrapper.getId()+" starts : " + timeBeforeStarts);
                Thread.sleep(timeBeforeStarts);
            }
            System.out.println(" Task before work is starting for Task id: " + taskWrapper.getId());
            for(CompletedTask ct: taskWrapper.getCompletedTasks()){
                if(taskConfiguration.getBeforeWorkScriptId()==null) {
                    this.runScript(this.taskPlugin.getTaskBeforeWorkJsContent(), ct);
                } else {
                    ExecutableScript es = executableScriptDao.get(taskConfiguration.getBeforeWorkScriptId());
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

    }

    @Override
    public void handleScriptFailure(ScriptException se) {
        _log.error("Before work script execution error for : " + taskPlugin.getTaskPluginName() + " - " + se.getMessage());
    }


}
