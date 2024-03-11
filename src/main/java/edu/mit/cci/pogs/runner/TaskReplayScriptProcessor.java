package edu.mit.cci.pogs.runner;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.openjdk.nashorn.api.scripting.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.service.EventLogService;
import edu.mit.cci.pogs.service.TaskService;
import edu.mit.cci.pogs.utils.DateUtils;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskReplayScriptProcessor implements Runnable {

    private Long sessionScriptToReplayFrom;

    private Long sessionToReplayFrom;

    private TaskWrapper taskWrapper;

    private SessionWrapper session;

    @Autowired
    private TaskService taskService;

    @Autowired
    private EventLogService eventLogService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ExecutableScriptDao executableScriptDao;

    private static final Logger _log = LoggerFactory.getLogger(TaskReplayScriptProcessor.class);

    private int SECONDS_BEFORE_TASK_STARTS = 7 * 1000;

    public void run(){

        Long timeBeforeStarts = (taskWrapper.getTaskStartTimestamp()) - (3*1000) - (DateUtils.now());
       try {
           if (timeBeforeStarts > 0) {
               _log.info(" Time until Task Replay Script Processor: " + taskWrapper.getId() + " starts : " + timeBeforeStarts + " - # OF CTS: " + taskWrapper.getCompletedTasks().size());
               Thread.sleep(timeBeforeStarts);
           }

           _log.info(" Task Replay Script Processor is starting for Task id: " + taskWrapper.getId());

           PaginatedTaskEventReplayRunner ptrr = (PaginatedTaskEventReplayRunner) context.getBean("paginatedTaskEventReplayRunner");
           ptrr.setSourceSessionId(sessionToReplayFrom);
           ptrr.setTaskWrapper(this.taskWrapper);
           ptrr.setSession(this.session);

           SessionRunner sr = SessionRunnerManager.getSessionRunner(session.getId());

           Thread thread = new Thread(ptrr);
           thread.start();
           sr.addThreadFromScript(thread);
       }catch (InterruptedException ie) {
           System.out.println("Stopping Task Replay Script Processor for task : " + taskWrapper.getId());
       }

    }



    public void setSessionToReplayFrom(Long sessionToReplayFrom) {
        this.sessionToReplayFrom = sessionToReplayFrom;
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


    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public ExecutableScriptDao getExecutableScriptDao() {
        return executableScriptDao;
    }

    public void setExecutableScriptDao(ExecutableScriptDao executableScriptDao) {
        this.executableScriptDao = executableScriptDao;
    }
}
