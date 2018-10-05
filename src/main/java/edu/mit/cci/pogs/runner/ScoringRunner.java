package edu.mit.cci.pogs.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.service.CompletedTaskService;
import edu.mit.cci.pogs.utils.DateUtils;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScoringRunner  implements Runnable {

    @Autowired
    private CompletedTaskService completedTaskService;

    private TaskWrapper taskWrapper;

    private SessionWrapper session;

    private static final Logger _log = LoggerFactory.getLogger(ScoringRunner.class);


    @Override
    public void run() {
        Long timeBeforeStarts = taskWrapper.getTaskEndTimestamp() - DateUtils.now();
        try {
            if (timeBeforeStarts > 0) {
                System.out.println(" Time until score for Task id: "+taskWrapper.getId()+" is done: " + timeBeforeStarts);
                Thread.sleep(timeBeforeStarts);
            }
            System.out.println(" Score is starting for Task id: " + taskWrapper.getId());
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
