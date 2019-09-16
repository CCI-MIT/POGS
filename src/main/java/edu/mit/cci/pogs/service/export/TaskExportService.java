package edu.mit.cci.pogs.service.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.dictionary.DictionaryDao;
import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.dao.sessionhastaskgroup.SessionHasTaskGroupDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskconfiguration.TaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskexecutionattribute.TaskExecutionAttributeDao;
import edu.mit.cci.pogs.model.dao.taskgroup.TaskGroupDao;
import edu.mit.cci.pogs.model.dao.taskgrouphastask.TaskGroupHasTaskDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
import edu.mit.cci.pogs.service.export.exportBeans.ExportFile;
import edu.mit.cci.pogs.service.export.exportBeans.TaskExport;
import edu.mit.cci.pogs.utils.ExportUtils;

@Service
public class TaskExportService {

    @Autowired
    private SessionHasTaskGroupDao sessionHasTaskGroupDao;

    @Autowired
    private TaskGroupHasTaskDao taskGroupHasTaskDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskHasTaskConfigurationDao taskHasTaskConfigurationDao;

    @Autowired
    private TaskExecutionAttributeDao taskExecutionAttributeDao;

    @Autowired
    private TaskGroupDao taskGroupDao;

    @Autowired
    private TaskConfigurationDao taskConfigurationDao;

    @Autowired
    private ExecutableScriptDao executableScriptDao;

    @Autowired
    private DictionaryDao dictionaryDao;

    public List<ExportFile> getTaskRelatedData(Long sessionId, Long studyId, String path) {


        List<ExportFile> efs = new ArrayList<>();
        List<SessionHasTaskGroup> shtgs = sessionHasTaskGroupDao.listSessionHasTaskGroupBySessionId(sessionId);

        List<TaskExport> tasksOfTaskGroup = new ArrayList<>();

        for (SessionHasTaskGroup shtg : shtgs) {
            List<TaskGroupHasTask> taskGroupHasTasks = taskGroupHasTaskDao.listByTaskGroupId(shtg.getTaskGroupId());
            TaskGroup taskGroup = taskGroupDao.get(shtg.getTaskGroupId());


            for (TaskGroupHasTask tght : taskGroupHasTasks) {
                Task t = taskDao.get(tght.getTaskId());

                TaskHasTaskConfiguration thtc = taskHasTaskConfigurationDao.getByTaskId(tght.getTaskId());
                TaskConfiguration tg = taskConfigurationDao.get(thtc.getTaskConfigurationId());


                TaskExport te = new TaskExport(t);

                te.setTaskGroupName(taskGroup.getTaskGroupName());
                te.setTaskGroupId(taskGroup.getId());
                te.setTaskConfigurationName(tg.getConfigurationName());
                if (tg.getBeforeWorkScriptId() != null) {
                    ExecutableScript beforeWork = executableScriptDao.get(tg.getBeforeWorkScriptId());
                    te.setTaskConfigurationBeforeWorkScriptName(beforeWork.getScriptName());
                } else {
                    te.setTaskConfigurationBeforeWorkScriptName("No script");
                }
                if (tg.getAfterWorkScriptId() != null) {
                    ExecutableScript afterWork = executableScriptDao.get(tg.getAfterWorkScriptId());
                    te.setTaskConfigurationBeforeWorkScriptName(afterWork.getScriptName());
                } else {
                    te.setTaskConfigurationBeforeWorkScriptName("No script");
                }
                if (tg.getScoreScriptId() != null) {
                    ExecutableScript scoreScript = executableScriptDao.get(tg.getScoreScriptId());
                    te.setTaskConfigurationBeforeWorkScriptName(scoreScript.getScriptName());
                } else {
                    te.setTaskConfigurationBeforeWorkScriptName("No script");
                }
                if (tg.getDictionaryId() != null) {
                    te.setTaskConfigurationDictionaryName(
                            dictionaryDao.get(tg.getDictionaryId()).getDictionaryName());
                } else {
                    te.setTaskConfigurationDictionaryName("No dictionary");
                }

                List<TaskExecutionAttribute> teas = taskExecutionAttributeDao
                        .listByTaskConfigurationId(thtc.getTaskConfigurationId());
                if(teas!=null) {
                    efs.addAll(ExportUtils.getEntityDataExportFile(path, TaskExecutionAttribute.class, teas,
                            studyId, sessionId,
                            null, thtc.getTaskId()));
                }


                tasksOfTaskGroup.add(te);
            }
        }


        efs.addAll(ExportUtils.getEntityDataExportFile(path, TaskExport.class,
                tasksOfTaskGroup, studyId,sessionId, null,null));

        return efs;
    }

    public List<Task> getTaskListInOrderOfTaskGroupForSession(Session session){

        List<SessionHasTaskGroup> shtgs = sessionHasTaskGroupDao
                .listSessionHasTaskGroupBySessionId(session.getId());

        List<Task> tasksInOrder = new ArrayList<>();
        for(SessionHasTaskGroup shtg: shtgs){
            TaskGroup tg = taskGroupDao.get(shtg.getTaskGroupId());
            List<TaskGroupHasTask> tghts = taskGroupHasTaskDao.listByTaskGroupId(tg.getId());
            for(TaskGroupHasTask tght : tghts){
                Task t = taskDao.get(tght.getTaskId());
                tasksInOrder.add(t);
            }
        }
        return tasksInOrder;
    }
}
