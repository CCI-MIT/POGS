package edu.mit.cci.pogs.service.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.completedtaskattribute.CompletedTaskAttributeDao;
import edu.mit.cci.pogs.model.dao.completedtaskscore.CompletedTaskScoreDao;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.dao.round.RoundDao;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.sessionhastaskgroup.SessionHasTaskGroupDao;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskScore;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.service.export.exportBeans.ExportFile;
import edu.mit.cci.pogs.service.export.exportBeans.SubjectExport;
import edu.mit.cci.pogs.utils.ExportUtils;

import static edu.mit.cci.pogs.constants.ApplicationConstants.COLLABORATION_MESSAGE;
import static edu.mit.cci.pogs.constants.ApplicationConstants.COMMUNICATION_MESSAGE;
import static edu.mit.cci.pogs.constants.ApplicationConstants.TASK_ATTRIBUTE;

@Service
public class SessionExportService {

    @Autowired
    private SessionDao sessionDao;
    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    @Autowired
    private CompletedTaskScoreDao completedTaskScoreDao;

    @Autowired
    private CompletedTaskAttributeDao completedTaskAttributeDao;

    @Autowired
    private TaskExportService taskExportService;

    @Autowired
    private SubjectExportService subjectExportService;

    @Autowired
    private RoundDao roundDao;

    @Autowired
    private TeamDao teamDao;


    public List<ExportFile> getSessionExportFiles(Long sessionId, Long studyId, String path) {
        List<ExportFile> ex = new ArrayList<>();

        List<EventLog> eventLogList = eventLogDao.listLogsBySessionId(sessionId);
        Map<Long, Long> completedTaskIdToSessionId = new HashMap<>();

        for (EventLog el : eventLogList) {
            completedTaskIdToSessionId.put(el.getCompletedTaskId(), el.getSessionId());
        }
        List<Long> completedTaskIds = new ArrayList<>();
        List<CompletedTask> completedTaskList = new ArrayList<>();
        for( Long id: completedTaskIdToSessionId.keySet()){
            completedTaskIds.add(id);
            completedTaskList.add(completedTaskDao.get(id));
        }

        ex.addAll(ExportUtils.getEntityDataExportFile(path,CompletedTask.class, completedTaskList, studyId,
                sessionId, null, null));

        ex.addAll(ExportUtils.getEntityDataExportFile(path,EventLog.class, eventLogList, studyId,
                sessionId, null, null));

        List<CompletedTaskScore> completedTaskScoreList =
                completedTaskScoreDao.listByCompletedTasksIds(completedTaskIds);

        ex.addAll(ExportUtils.getEntityDataExportFile(path, CompletedTaskScore.class, completedTaskScoreList,
                studyId, sessionId,completedTaskIdToSessionId,null));


        List<CompletedTaskAttribute> completedTaskAttributesList =
                completedTaskAttributeDao.listByCompletedTasksIds(completedTaskIds);
        ex.addAll(ExportUtils.getEntityDataExportFile(path, CompletedTaskAttribute.class,
                completedTaskAttributesList,
                studyId, sessionId, completedTaskIdToSessionId,null));

        //ONE FILE

        ex.addAll(taskExportService.getTaskRelatedData(sessionId, studyId, path));

        ex.addAll(subjectExportService.getSubjectAndTeamsFiles(sessionId, studyId, path));


        //subjects and teams.
        return ex;
    }

    public ExportFile exportAggregateSubjectParticipation(Long studyId, String path){
        StringBuilder val = new StringBuilder();
        StringBuilder header = new StringBuilder();
        header.append("Session Name,Task Name,Team Id,Number of Subjects,Task Group Name,Subject External Ids," +
                "Communication Count,Task Attribute Count,Collaboration Edit Count");

        //get all sessions
        List<Session> sessionInStudy = sessionDao.listByStudyId(studyId);

        for (Session session : sessionInStudy) {
            List<EventLog> eventLogList = eventLogDao.listLogsBySessionId(session.getId());
            Set<Long> completedTaskIds = new HashSet<>();
            for (EventLog eventLog : eventLogList) {
                completedTaskIds.add(eventLog.getCompletedTaskId());
            }
            //get completed tasks
            List<CompletedTask> completedTasks = new ArrayList<>();
            completedTasks = completedTaskDao.listByCompletedTaskIds(new ArrayList<>(completedTaskIds));
            //get team ids
            Set<Long> teamIds = new HashSet<>();
            for (CompletedTask completedTask : completedTasks)
                teamIds.add(completedTask.getTeamId());

            for (Long completedTaskId : completedTaskIds) {
                for (Long teamId : teamIds) {
                    val.append(session.getSessionSuffix() + ",");
                    val.append(completedTaskId + ",");
                    val.append(teamId + ",");
                    List<Long> subjectIds = new ArrayList<>();
                    subjectIds = completedTaskDao.listSubjectIds(teamId);
                    val.append(subjectIds.size() + ",");
                    StringBuilder subjects = new StringBuilder();
                    subjects.append("[");
                    StringBuilder communication = new StringBuilder();
                    communication.append("[");
                    StringBuilder collaboration = new StringBuilder();
                    collaboration.append("[");
                    StringBuilder taskAttribute = new StringBuilder();
                    taskAttribute.append("[");
                    for (Long subjectId : subjectIds) {
                        subjects.append(subjectId + ",");
                        communication.append(eventLogDao.getCountOfSubjectContribution
                                (subjectId, completedTaskId, COMMUNICATION_MESSAGE) + ",");
                        collaboration.append(eventLogDao.getCountOfSubjectContribution
                                (subjectId, completedTaskId, COLLABORATION_MESSAGE) + ",");
                        taskAttribute.append(eventLogDao.getCountOfSubjectContribution
                                (subjectId, completedTaskId, TASK_ATTRIBUTE) + ",");
                    }
                    //TODO add task group name - Where is it available?
                    val.append(subjects + "],");
                    val.append(communication + "],");
                    val.append(collaboration + "],");
                    val.append(taskAttribute + "],");
                    val.append("\n");
                }
            }
            val.append("\n");
        }

        String fileName = "SubjectContribution_Aggregate_" + String.valueOf(studyId) + "_" + ExportUtils.getTimeFormattedNoSpaces(new Timestamp(new Date().getTime()))  + ".csv";

        ExportFile ex = new ExportFile();
        ex.setFileContent(val.toString());
        ex.setFileHeader(header.toString());
        ex.setFileName(fileName);
        ex.setFileRootPath(path);
        ex.setFileType("SubjectContribution");
        return ex;

    }



    public ExportFile exportAggregateStudyScoreReport(Long studyId, String path){

        StringBuilder val = new StringBuilder();
        StringBuilder header = new StringBuilder();
        header.append("Session Name, Team Id");



        //for all sessions
        List<Session> sessionInStudy = sessionDao.listByStudyId(studyId);

        boolean allSessionHaveTheSameTaskGroupConfig = true;

        Map<Long,Integer> tasksMapCount = new LinkedHashMap<>();
        Map<Long,Task> tasksMap = new LinkedHashMap<>();
        if(sessionInStudy!=null && ! sessionInStudy.isEmpty()){
            for(Session s : sessionInStudy) {
                List<Task> tasksInOrder = taskExportService
                        .getTaskListInOrderOfTaskGroupForSession(sessionInStudy.get(0));

                for(Task t: tasksInOrder){

                    Integer count = tasksMapCount.get(t.getId());
                    if(count  == null){
                        tasksMap.put(t.getId(), t);
                        tasksMapCount.put(t.getId(),0);
                        count = 0;
                    }
                    tasksMapCount.put(t.getId(),(count + 1));
                }

            }
        }



        for (Session session : sessionInStudy) {

                val.append(calculateScoresForTasksInMap(tasksMap,session));

        }

        String fileName = "TaskScores_Aggregate_" + String.valueOf(studyId) + "_" + ExportUtils.getTimeFormattedNoSpaces(new Timestamp(new Date().getTime())) + ".csv";
        ExportFile ex = new ExportFile();
        ex.setFileRootPath(path);
        ex.setFileHeader(header.toString());
        ex.setFileContent(val.toString());
        ex.setFileName(fileName);
        return ex;

    }

    private String calculateScoresForTasksInMap(Map<Long, Task> tasksMap, Session session) {

        //get all completed tasks and for each task in the taskmap add the values.

        List<Round> rounds = roundDao.listBySessionId(session.getId());
        List<SubjectExport> subjectExports = new ArrayList<>();
        List<ExportFile> exportFiles = new ArrayList<>();
        StringBuilder scores = new StringBuilder();
        for(Round round: rounds) {
            List<Team> teams = teamDao.listByRoundId(round.getId());

            for (Team team : teams) {
                List<CompletedTask> completedTasks =
                        completedTaskDao.listByRoundIdTeamId(round.getId(), team.getId());

                Map<Long,Double> scoreMap = new LinkedHashMap<>();
                for(Long taskId : tasksMap.keySet()){
                    scoreMap.put(taskId,0d);
                }
                for(CompletedTask ct: completedTasks){
                    Double score = completedTaskScoreDao.getScore(ct.getId());
                    if(score == null){
                        score = 0.0;
                    }
                    scoreMap.put(ct.getTaskId(),score);
                }
                scores.append(session.getFullSessionName() + ",");
                scores.append(team.getId() + ",");
                for(Long taskId : tasksMap.keySet()){
                    scores.append(tasksMap.get(taskId)+ ",");
                }
                scores.append("\n");
            }
        }

        return scores.toString();
    }


}
