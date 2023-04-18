package edu.mit.cci.pogs.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.script.ScriptContext;
import javax.script.ScriptException;

import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.service.CompletedTaskScoreService;
import edu.mit.cci.pogs.service.CompletedTaskService;
import edu.mit.cci.pogs.service.IndividualSubjectScoreService;
import edu.mit.cci.pogs.utils.DateUtils;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScoringRunner extends TaskRelatedScriptRunner implements Runnable {


    @Autowired
    private CompletedTaskService completedTaskService;

    @Autowired
    private CompletedTaskScoreService completedTaskScoreService;

    @Autowired
    private IndividualSubjectScoreService individualSubjectScoreService;

    private CompletedTask completedTask;




    private static final Logger _log = LoggerFactory.getLogger(ScoringRunner.class);


    @Override
    public void run() {

        Long bufferForAfterWork = 0l;
        taskConfiguration = taskService.getTaskConfiguration(taskWrapper.getId());

        if(taskConfiguration.getAfterWorkScriptId()!=null || this.taskPlugin.getTaskAfterWorkJsContent()!=null){
            bufferForAfterWork = 10*1000l;
        }

        Long timeBeforeStarts = taskWrapper.getTaskEndTimestampWithoutScoringPage() + bufferForAfterWork - DateUtils.now();
        taskPlugin =  TaskPlugin.getTaskPlugin(taskWrapper.getTaskPluginType());

        try {
            if (timeBeforeStarts > 0) {
                System.out.println(" Time until score for Task id: " + taskWrapper.getId() + " starts : " + timeBeforeStarts);
                Thread.sleep(timeBeforeStarts);
            }
            System.out.println(" Score is starting for Task id: " + taskWrapper.getId() + " (" + taskWrapper.getTaskName()+" )");

            if (this.taskPlugin.isScriptType()) {
                if(taskConfiguration.getScoreScriptId()==null) {
                    this.runScript(this.taskPlugin.getTaskScoreJsContent(), getCompletedTask());
                } else {
                    ExecutableScript es = executableScriptDao.get(taskConfiguration.getScoreScriptId());
                    this.runScript(es.getScriptContent(),getCompletedTask());
                }
            } else {
                if(taskConfiguration.getScoreScriptId()==null) {
                     completedTaskService.scoreCompletedTask(getCompletedTask(), taskWrapper);
                } else {
                    ExecutableScript es = executableScriptDao.get(taskConfiguration.getScoreScriptId());
                    this.runScript(es.getScriptContent(),getCompletedTask());
                }
            }


        } catch (InterruptedException ie) {
            _log.info("Stopping scoring script for task : " + taskWrapper.getId());
        }

    }

    protected void retrieveScore() {
        String attributesToAddJson = (String) this.getEngine().getBindings(
                ScriptContext.ENGINE_SCOPE).get("completedTaskScore");

        completedTaskScoreService.createCompletedTaskScoreFromScript(
                attributesToAddJson,this.getCompletedTask().getId());

        String individualScoreJson = (String) this.getEngine().getBindings(
                ScriptContext.ENGINE_SCOPE).get("individualSubjectScores");

        individualSubjectScoreService.createIndividualSubjectScoreFromScript(individualScoreJson,
                this.getCompletedTask().getId());


    }

    @Override
    public void retrieveScriptVariables() {

        retreiveSubjectAttributesToAdd();
        retrieveCompletedTaskAttributesToAdd();
        retrieveScore();
    }

    @Override
    public void handleScriptFailure(ScriptException se) {
        _log.error("Task scoring runner script execution error for : " + taskPlugin.getTaskPluginName() + " - " + se.getMessage());
    }

}
