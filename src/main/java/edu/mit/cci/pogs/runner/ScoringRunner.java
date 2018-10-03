package edu.mit.cci.pogs.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.service.CompletedTaskService;

@Component
public class ScoringRunner  implements Runnable {

    @Autowired
    private CompletedTaskService completedTaskService;

    private TaskWrapper taskWrapper;

    private SessionWrapper session;

    private static final Logger _log = LoggerFactory.getLogger(ChatScriptRunner.class);

    @Override
    public void run() {
        Long timeBeforeStarts = taskWrapper.getTaskEndTimestamp() - new Date().getTime();
        try {
            if (timeBeforeStarts > 0) {
                Thread.sleep(timeBeforeStarts);
            }
            for(CompletedTask ct: taskWrapper.getCompletedTasks()){
                completedTaskService.scoreCompletedTask(ct, taskWrapper);
            }

        } catch (InterruptedException ie) {
            _log.info("Stopping scoring script for task : " + taskWrapper.getId());
        }

    }

    public TaskWrapper getTaskWrapper() {
        return taskWrapper;
    }

    public void setTaskWrapper(TaskWrapper taskWrapper) {
        this.taskWrapper = taskWrapper;
    }

    public SessionWrapper getSession() {
        return session;
    }

    public void setSession(SessionWrapper session) {
        this.session = session;
    }
}
