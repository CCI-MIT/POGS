package edu.mit.cci.pogs.service.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.cci.pogs.model.dao.dictionary.DictionaryDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryDao;
import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.dao.fileentry.FileEntryDao;
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
import edu.mit.cci.pogs.model.jooq.tables.pojos.FileEntry;
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

    @Autowired
    private FileEntryDao fileentryDao;

    @Autowired
    private Environment env;

    private static final Long RESEARCH_GROUP_ID = 1l;

    public List<ExportFile> generateStudyConfigurationSQL(Long studyId, String path) {

        String imagePath = env.getProperty("images.dir");

        List<Session> sessions = sessionDao.listByStudyId(studyId);
        List<Session> parentSession = new ArrayList<>();
        List<TaskGroup> taskGroupList = new ArrayList<>();
        List<TaskGroupHasTask> taskGroupHasTasks = new ArrayList<>();
        List<SessionHasTaskGroup> sessionHasTaskGroupsTemp;
        List<SessionHasTaskGroup> sessionHasTaskGroups = new ArrayList<>();
        List<Task> taskList = new ArrayList<>();
        Map<Long,TaskConfiguration> taskConfigurations = new HashMap<>();

        List<TaskHasTaskConfiguration> taskHasTaskConfigurations = new ArrayList<>();

        List<TaskExecutionAttribute> taskExecutionAttributes = new ArrayList<>();
        Map<Long,Dictionary> dictionaryList = new HashMap<>();
        List<DictionaryEntry> dictionaryEntryList = new ArrayList<>();

        List<ExecutableScript> executableScriptList = new ArrayList<>();

        List<ExportFile> exportFiles = new ArrayList<>();

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
            TaskHasTaskConfiguration taskHasTaskConfigurationz =
                    taskHasTaskConfigurationDao.getByTaskId(tght.getTaskId());
            TaskConfiguration tg = taskConfigurationDao.get(
                    taskHasTaskConfigurationz.getTaskConfigurationId());
            taskConfigurations.put(tg.getId(), tg);
            taskHasTaskConfigurations.add(taskHasTaskConfigurationz);

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
        String buffer = "";
        //study
        buffer += SQLUtils.getBasicSetup();
        Study study = studyDao.get(studyId);
        buffer += SQLUtils.getSQLInsertFromPojo(study);
        buffer+=generateResearchGroupInsert("study_has_research_group",
                "study_id",studyId +"");

        //session
        for(Session s: parentSession){
            buffer += SQLUtils.getSQLInsertFromPojo(s);
        }

        //task group
        for(TaskGroup tg: taskGroupList){
            buffer += SQLUtils.getSQLInsertFromPojo(tg);
            buffer+=generateResearchGroupInsert("task_group_has_research_group",
                    "task_group_id",tg.getId() +"");
        }

        //session has task group
        for(SessionHasTaskGroup shtg: sessionHasTaskGroups ){
            buffer += SQLUtils.getSQLInsertFromPojo(shtg);
        }



        //task
        for(Task t: taskList){
            buffer += SQLUtils.getSQLInsertFromPojo(t);
            buffer+=generateResearchGroupInsert("task_has_research_group","task_id",t.getId() + "");
        }

        //task config
        for(TaskConfiguration tc: taskConfigurations.values()){
            buffer += SQLUtils.getSQLInsertFromPojo(tc);
            buffer+=generateResearchGroupInsert("task_configuration_has_research_group",
                    "task_configuration_id",tc.getId() +"");
        }

        for(TaskHasTaskConfiguration thtc: taskHasTaskConfigurations){
            buffer += SQLUtils.getSQLInsertFromPojo(thtc);
        }

        //task execution attribute
        List<Long> imageReferencesInAttributes = new ArrayList<>();

        for(TaskExecutionAttribute tea: taskExecutionAttributes){
            buffer += SQLUtils.getSQLInsertFromPojo(tea);
            imageReferencesInAttributes.addAll(retrieveImageReferenceFromAttribute(tea));
        }


        //task group has task
        for(TaskGroupHasTask tght: taskGroupHasTasks){
            buffer += SQLUtils.getSQLInsertFromPojo(tght);

        }

        //dictionary
        for(Dictionary d: dictionaryList.values()){
            buffer += SQLUtils.getSQLInsertFromPojo(d);
            buffer+=generateResearchGroupInsert("dictionary_has_research_group",
                    "dictionary_id",d.getId() +"");
        }

        //dictionary entry
        for(DictionaryEntry de: dictionaryEntryList){
            buffer += SQLUtils.getSQLInsertFromPojo(de);
        }

        //executable script
        for(ExecutableScript es: executableScriptList ){
            buffer += SQLUtils.getSQLInsertFromPojo(es);
            buffer+=generateResearchGroupInsert("executable_script_has_research_group",
                    "executable_script_id",es.getId() +"");
        }

        Map<Long, Long> images = new HashMap<>();
        for(Long imageRefs: imageReferencesInAttributes){
            images.put(imageRefs, imageRefs);
        }
        List<FileEntry> filesReferenced = new ArrayList<>();
        for(Long fileEntryId: images.values()){
            filesReferenced.add(fileentryDao.get(fileEntryId));
        }
        for(FileEntry fileEntry: filesReferenced){
            ExportFile ef = new ExportFile();

            ef.setFileRootPath(path);
            ef.setShouldCopy(true);
            //ef.setRelativeFolder(".imagesfiles");
            ef.setPathOfOriginFile(imagePath+"/fileEntries/"+fileEntry.getId()+"."+fileEntry.getFileEntryExtension());
            ef.setFileName(fileEntry.getId().toString());
            ef.setFileType(fileEntry.getFileEntryExtension());
            exportFiles.add(ef);

            buffer += SQLUtils.getSQLInsertFromPojo(fileEntry);
        }





        ExportFile ef = new ExportFile();
        ef.setFileContent(buffer);
        ef.setFileRootPath(path);
        ef.setFileName("dump.sql");
        ef.setFileType("sql");

        exportFiles.add(ef);
        return exportFiles;
    }

    private ArrayList<Long> retrieveImageReferenceFromAttribute(TaskExecutionAttribute tea) {
        //System.out.println(tea.getStringValue());
        ArrayList<Long> ret = new ArrayList<>();
        //Pattern pattern = Pattern.compile("src=\"/images/(.*?)\"");
        Pattern pattern = Pattern.compile("src=\\\\\\\"/images/(.*?)\\\\\\\"");
        Matcher matcher = pattern.matcher(tea.getStringValue());
        while (matcher.find()) {
            //System.out.println(matcher.group(1));
            ret.add(new Long(matcher.group(1)));
        }

        return ret;
    }

    private static String generateResearchGroupInsert(String tableName, String fieldName, String value){
        return "INSERT INTO `"+tableName+"` (`"+fieldName+"`, `research_group_id`) VALUES ('"+value +"', '"+RESEARCH_GROUP_ID+"');\n";

    }
}
