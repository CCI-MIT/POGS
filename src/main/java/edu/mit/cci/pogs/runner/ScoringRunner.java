package edu.mit.cci.pogs.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;

import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.service.CompletedTaskService;
import edu.mit.cci.pogs.utils.DateUtils;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScoringRunner extends TaskRelatedScriptRunner implements Runnable {


    @Autowired
    private CompletedTaskService completedTaskService;

    private static final Logger _log = LoggerFactory.getLogger(ScoringRunner.class);


    @Override
    public void run() {
        Long timeBeforeStarts = taskWrapper.getTaskEndTimestamp() - DateUtils.now();

        taskPlugin =  TaskPlugin.getTaskPlugin(taskWrapper.getTaskPluginType());
        taskConfiguration = taskService.getTaskConfiguration(taskWrapper.getId());
        try {
            if (timeBeforeStarts > 0) {
                System.out.println(" Time until score for Task id: " + taskWrapper.getId() + " is done: " + timeBeforeStarts);
                Thread.sleep(timeBeforeStarts);
            }
            System.out.println(" Score is starting for Task id: " + taskWrapper.getId());
            for (CompletedTask ct : taskWrapper.getCompletedTasks()) {
                if (this.taskPlugin.isScriptType()) {
                    this.runScript(this.taskPlugin.getTaskScoreJsContent(), ct);
                } else {
                    if(taskConfiguration.getScoreScriptId()==null) {
                        completedTaskService.scoreCompletedTask(ct, taskWrapper);
                    } else {
                        ExecutableScript es = executableScriptDao.get(taskConfiguration.getScoreScriptId());
                        this.runScript(es.getScriptContent(),ct);
                    }
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
