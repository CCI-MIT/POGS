package edu.mit.cci.pogs.service.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.cci.pogs.model.dao.dictionary.DictionaryDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryDao;
import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.sessionhastaskgroup.SessionHasTaskGroupDao;
import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskconfiguration.TaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskexecutionattribute.TaskExecutionAttributeDao;
import edu.mit.cci.pogs.model.dao.taskgroup.TaskGroupDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
import edu.mit.cci.pogs.service.TaskConfigurationService;
import edu.mit.cci.pogs.service.TaskGroupService;
import edu.mit.cci.pogs.service.export.exportBeans.ExportFile;
import edu.mit.cci.pogs.utils.SQLUtils;

@Service
public class SQLExportService {

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private TaskGroupService taskGroupService;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private StudyDao studyDao;

    @Autowired
    private TaskGroupDao taskGroupDao;

    @Autowired
    private TaskConfigurationDao taskConfigurationDao;

    @Autowired
    private TaskHasTaskConfigurationDao taskHasTaskConfigurationDao;

    @Autowired
    private SessionHasTaskGroupDao sessionHasTaskGroupDao;

    @Autowired
    private TaskExecutionAttributeDao taskExecutionAttributeDao;

    @Autowired
    private DictionaryDao dictionaryDao;

    @Autowired
    private DictionaryEntryDao dictionaryEntryDao;

    @Autowired
    private ExecutableScriptDao executableScriptDao;

    public List<ExportFile> generateStudyConfigurationSQL(Long studyId, String path) {
        List<Session> sessions = sessionDao.listByStudyId(studyId);
        List<Session> parentSession = new ArrayList<>();
        List<TaskGroup> taskGroupList = new ArrayList<>();
        List<TaskGroupHasTask> taskGroupHasTasks = new ArrayList<>();
        List<SessionHasTaskGroup> sessionHasTaskGroupsTemp;
        List<SessionHasTaskGroup> sessionHasTaskGroups = new ArrayList<>();
        List<Task> taskList = new ArrayList<>();
        List<TaskConfiguration> taskConfigurations = new ArrayList<>();
        List<TaskExecutionAttribute> taskExecutionAttributes = new ArrayList<>();
        Map<Long,Dictionary> dictionaryList = new HashMap<>();
        List<DictionaryEntry> dictionaryEntryList = new ArrayList<>();

        List<ExecutableScript> executableScriptList = new ArrayList<>();

        for(Session s: sessions){
            if(s.getParentSessionId()==null){
                parentSession.add(s);
                sessionHasTaskGroupsTemp = sessionHasTaskGroupDao.listSessionHasTaskGroupBySessionId(s.getId());
                sessionHasTaskGroups.addAll(sessionHasTaskGroupsTemp);
                for(SessionHasTaskGroup shtg: sessionHasTaskGroupsTemp) {
                    taskGroupHasTasks.addAll(taskGroupService.listTaskGroupHasTaskByTaskGroup(shtg.getTaskGroupId()));
                    TaskGroup taskGroup = taskGroupDao.get(shtg.getTaskGroupId());
                    taskGroupList.add(taskGroup);
                }

            }
        }
        for(TaskGroupHasTask tght: taskGroupHasTasks){
            taskList.add(taskDao.get(tght.getTaskId()));
            TaskHasTaskConfiguration taskHasTaskConfigurations =
                    taskHasTaskConfigurationDao.getByTaskId(tght.getTaskId());
            TaskConfiguration tg = taskConfigurationDao.get(
                    taskHasTaskConfigurations.getTaskConfigurationId());
            taskConfigurations.add(tg);
            taskExecutionAttributes.addAll(
                    taskExecutionAttributeDao.listByTaskConfigurationId(tg.getId()));

            if(tg.getDictionaryId()!=null) {
                dictionaryList.put((tg.getDictionaryId()),dictionaryDao.get(tg.getDictionaryId()));
                dictionaryEntryList.addAll(dictionaryEntryDao
                        .listDictionaryEntriesByDictionary(tg.getDictionaryId()));
            }
            if(tg.getAfterWorkScriptId()!=null ||
                    tg.getBeforeWorkScriptId()!=null ||
            tg.getScoreScriptId()!=null){
                ExecutableScript es = executableScriptDao.get(tg.getAfterWorkScriptId());
                if(es!=null){
                    executableScriptList.add(es);
                }
                es = executableScriptDao.get(tg.getBeforeWorkScriptId());
                if(es!=null){
                    executableScriptList.add(es);
                }
                es = executableScriptDao.get(tg.getScoreScriptId());
                if(es!=null){
                    executableScriptList.add(es);
                }
            }
        }
        String buffer = "SET FOREIGN_KEY_CHECKS=0;\n";
        //study
        Study study = studyDao.get(studyId);
        buffer += SQLUtils.getSQLInsertFromPojo(study);


        //session
        for(Session s: parentSession){
            buffer += SQLUtils.getSQLInsertFromPojo(s);
        }

        //task group
        for(TaskGroup tg: taskGroupList){
            buffer += SQLUtils.getSQLInsertFromPojo(tg);
        }

        //session has task group
        for(SessionHasTaskGroup shtg: sessionHasTaskGroups ){
            buffer += SQLUtils.getSQLInsertFromPojo(shtg);
        }

        //task group has task
        for(TaskGroupHasTask tght: taskGroupHasTasks){
            buffer += SQLUtils.getSQLInsertFromPojo(tght);
        }

        //task
        for(Task t: taskList){
            buffer += SQLUtils.getSQLInsertFromPojo(t);
        }

        //task config
        for(TaskConfiguration tc: taskConfigurations){
            buffer += SQLUtils.getSQLInsertFromPojo(tc);
        }

        //task execution attribute
        for(TaskExecutionAttribute tea: taskExecutionAttributes){
            buffer += SQLUtils.getSQLInsertFromPojo(tea);
        }

        //dictionary
        for(Dictionary d: dictionaryList.values()){
            buffer += SQLUtils.getSQLInsertFromPojo(d);
        }

        //dictionary entry
        for(DictionaryEntry de: dictionaryEntryList){
            buffer += SQLUtils.getSQLInsertFromPojo(de);
        }

        //executable script
        for(ExecutableScript es: executableScriptList ){
            buffer += SQLUtils.getSQLInsertFromPojo(es);
        }

        buffer+= "\nSET FOREIGN_KEY_CHECKS=1;\n";

        ExportFile ef = new ExportFile();
        ef.setFileContent(buffer);
        ef.setFileRootPath(path);
        ef.setFileName("dump.sql");
        ef.setFileType("sql");
        List<ExportFile> exportFiles = new ArrayList<>();
        exportFiles.add(ef);
        return exportFiles;
    }
}
