package edu.mit.cci.pogs.service.summaryexport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.completedtaskscore.CompletedTaskScoreDao;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.dao.export.CompletedTaskScoreExport;
import edu.mit.cci.pogs.model.dao.export.CompletedTaskScoreSubjectTeamExport;
import edu.mit.cci.pogs.model.dao.export.EventLogCheckingSummary;
import edu.mit.cci.pogs.model.dao.export.EventLogExport;
import edu.mit.cci.pogs.model.dao.export.ExportDao;
import edu.mit.cci.pogs.model.dao.export.IndividualSubjectScoreExport;
import edu.mit.cci.pogs.model.dao.export.SubjectExport;
import edu.mit.cci.pogs.model.dao.individualsubjectscore.IndividualSubjectScoreDao;
import edu.mit.cci.pogs.model.dao.round.RoundDao;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.session.SessionScheduleType;
import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskScore;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.IndividualSubjectScore;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.runner.TaskSnapshotExportRunner;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.service.CompletedTaskScoreService;
import edu.mit.cci.pogs.service.IndividualSubjectScoreService;
import edu.mit.cci.pogs.service.SessionService;
import edu.mit.cci.pogs.service.SubjectService;
import edu.mit.cci.pogs.service.TaskGroupService;
import edu.mit.cci.pogs.service.TaskService;
import edu.mit.cci.pogs.service.TeamService;
import edu.mit.cci.pogs.service.export.exportBeans.ExportFile;
import edu.mit.cci.pogs.service.export.exportBeans.SessionRelatedScore;
import edu.mit.cci.pogs.service.export.exportBeans.SubjectStudyScore;
import edu.mit.cci.pogs.utils.ExportUtils;

@Service
public class SummaryExportService {

    @Autowired
    private ExportDao exportDao;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private StudyDao studyDao;

    @Autowired
    private CompletedTaskScoreDao completedTaskScoreDao;

    @Autowired
    private IndividualSubjectScoreDao individualSubjectScoreDao;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private RoundDao roundDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private TaskGroupService taskGroupService;

    private List<Long> getSessionIds(Long studyId, Long sessionId) {
        List<Long> sessionList = new ArrayList<>();
        if (studyId != null) {
            for (Session su : sessionDao.listByStudyId(studyId)) {
                if (!su.getSessionScheduleType().equals(SessionScheduleType.PERPETUAL.name())) {
                    sessionList.add(su.getId());
                }
            }

        } else {
            Session session = sessionDao.get(sessionId);
            if (session.getParentSessionId() == null) {
                //handle the parent id
                List<Session> sessions = sessionDao.listByParentSessionId(session.getId());
                sessions.forEach((se) -> sessionList.add(se.getId()));
                if(sessions.size() == 0){
                    sessionList.add(session.getId());
                }
            } else {
                sessionList.add(session.getId());
            }
        }
        return sessionList;
    }

    public ExportFile getPresenceSummaryTable(Long studyId, Long sessionId, String path) {
        List<Long> sessionList = getSessionIds(studyId, sessionId);
        List<EventLogCheckingSummary> eventLogList = exportDao.getEventLogCheckIn(sessionList);
        List<EventLogCheckingSummary> eventLogExports = new ArrayList<>();
        Map<Long, Map<Long, EventLogCheckingSummary>> groupedByTask = new HashMap<>();

        Map<Long, String> subjectIds = new HashMap<>();
        for (EventLogCheckingSummary elcs : eventLogList) {
            if (groupedByTask.get(elcs.getCompletedTaskId()) == null) {
                groupedByTask.put(elcs.getCompletedTaskId(), new HashMap<>());
            }
            groupedByTask.get(elcs.getCompletedTaskId()).put(elcs.getSubjectId(), elcs);
            if (subjectIds.get(elcs.getSubjectId()) == null) {
                subjectIds.put(elcs.getSubjectId(), elcs.getSubjectExternalId());
            }
        }

        for (Long compTaskId : groupedByTask.keySet()) {
            Map<Long, EventLogCheckingSummary> summaryMapBySubject = groupedByTask.get(compTaskId);
            EventLogCheckingSummary elcs = null;

            for (Long subjectId : subjectIds.keySet()) {
                EventLogCheckingSummary subjectEventLog = summaryMapBySubject.get(subjectId);
                if (subjectEventLog != null) {
                    if (elcs == null) {
                        elcs = subjectEventLog;
                        elcs.setSubjectsNames(new ArrayList<>());
                        elcs.setSubjectsPingCount(new ArrayList<>());
                    }
                    elcs.getSubjectsNames().add(subjectEventLog.getSubjectExternalId());
                    elcs.getSubjectsPingCount().add(subjectEventLog.getSubjectCount() + "");
                }
            }
            eventLogExports.add(elcs);

        }


        String[] fieldOrder = {
                "studyPrefix",
                "sessionSuffix",
                "sessionStartDate",
                "sessionId",
                "teamId",
                "taskName",
                "subjectsNames",
                "subjectsPingCount"
        };

        return ExportUtils.getExportFileForSimplePojo(path, "EventLogCheckIns_" +
                        ((studyId != null) ? (studyId) : (sessionId)), EventLogCheckingSummary.class, eventLogExports,
                Arrays.asList(fieldOrder));

    }

    public ExportFile exportEventLog(Long studyId, Long sessionId, String path) {

        List<Long> sessionList = getSessionIds(studyId, sessionId);

        List<EventLogExport> eventLogExports = exportDao.getEventLogExportInfo(sessionList);
        for (EventLogExport ele : eventLogExports) {
            ele.setEventContent("");
            ele.setSummaryDescription(validateSummary(ele.getSummaryDescription()));
            if (ele.getReceiverId() != null) {
                Subject su = subjectDao.get(ele.getReceiverId());
                ele.setReceiverSubjectExternalId(su.getSubjectExternalId());
            } else {
                ele.setReceiverSubjectExternalId("");
            }

        }

        String[] fieldOrder = {
                "studyPrefix",
                "sessionSuffix",
                "sessionStartDate",
                "sessionId",
                "taskName",
                "soloTask",
                "eventType",
                "eventContent",
                "timestamp",
                "senderSubjectExternalId",
                "receiverSubjectExternalId",
                "summaryDescription"
        };

        return ExportUtils.getExportFileForSimplePojo(path, "EventLog_" +
                        ((studyId != null) ? (studyId) : (sessionId)), EventLogExport.class, eventLogExports,
                Arrays.asList(fieldOrder));
    }


    private static String validateSummary(String summaryDescription) {
        if (summaryDescription == null) return "";
        summaryDescription = summaryDescription.replaceAll("\\n", "¶");
        summaryDescription = summaryDescription.replaceAll(",", "|");
        summaryDescription = summaryDescription.replaceAll(";", "|");
        return summaryDescription;
    }

    public List<ExportFile> exportTaskSummaryFiles(Long studyId, Long sessionId, String path) {
        return null;
    }

    public ExportFile exportSubjectSummaryFiles(Long studyId, Long sessionId, String path) {

        List<Long> sessionList = getSessionIds(studyId, sessionId);
        List<SubjectExport> exports = new ArrayList<>();
        Map<String, String> subjectAttributeNames = new HashMap<>();
        Study study = null;

        if (studyId != null) {
            study = studyDao.get(studyId);
        }
        for (Long sessionIdz : sessionList) {
            List<Subject> subjects = subjectDao.listBySessionIdOrParentSessionId(sessionIdz);
            Session session = sessionDao.get(sessionIdz);
            if (studyId == null) {
                study = studyDao.get(session.getStudyId());
            }
            for (Subject su : subjects) {
                List<SubjectAttribute> subjectAttributes = subjectService.getSubjectAttributes(su.getId());
                SubjectExport se = new SubjectExport(su);
                se.setStudyPrefix(study.getStudySessionPrefix());
                se.setSessionSuffix(session.getSessionSuffix());
                se.setSessionStartDate(session.getSessionStartDate());
                se.setSessionId(session.getId());
                List<EventLog> eventLogs = eventLogDao.listCheckInSubjectLogs(su.getId());
                if (eventLogs != null && eventLogs.size() > 0) {
                    se.setLastCheckInPage(eventLogs.get(0).getSummaryDescription());
                    se.setLastCheckInTime(getTimeFormatted(eventLogs.get(0).getTimestamp()));
                } else {
                    se.setLastCheckInPage("");
                    se.setLastCheckInTime("");
                }

                Team team = teamDao.getSubjectTeam(su.getId(), null, null, null);
                if (team != null) {
                    se.setTeamId(team.getId().toString());
                }
                if (subjectAttributes != null && !subjectAttributes.isEmpty()) {
                    for (SubjectAttribute sa : subjectAttributes) {
                        se.addSubjectAttribute(sa);
                        subjectAttributeNames.put(sa.getAttributeName(), sa.getAttributeName());
                    }
                }
                exports.add(se);
            }
        }

        List<String> list = new ArrayList<>();
        list.addAll(subjectAttributeNames.keySet());

        Collections.sort(list);

        for (SubjectExport se : exports) {
            se.setAttributes(list);
        }

        String[] fieldOrder = {"studyPrefix",
                "sessionSuffix",
                "sessionStartDate",
                "sessionId",
                "subjectId",
                "subjectExternalId",
                "subjectDisplayName",
                "subjectPreviousSessionSubjectId",
                "lastCheckInPage",
                "lastCheckInTime"
        };


        return ExportUtils.getExportFileForSimplePojoWithExtraColumns(
                path,
                "Subjects_" + ((studyId != null) ? ("study[" + studyId + "]") : ("session[" + sessionId + "]")),
                SubjectExport.class,
                exports,
                Arrays.asList(fieldOrder),
                list);


    }

    private static String getTimeFormatted(Timestamp timestamp) {
        if (timestamp == null) return "";
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(new Date(timestamp.getTime()));
    }

    public ExportFile exportSubjectIndividualScoreSummaryFiles(Long studyId, Long sessionId, String path) {

        List<Long> sessionList = getSessionIds(studyId, sessionId);

        List<IndividualSubjectScoreExport> eventLogExports = exportDao.getIndividualSubjectScoreExportInfo(sessionList);


        String[] fieldOrder = {"studyPrefix",
                "sessionSuffix",
                "sessionStartDate",
                "sessionId",
                "taskName",
                "soloTask",
                "subjectId",
                "subjectExternalId",
                "totalScore"};

        return ExportUtils.getExportFileForSimplePojo(path,
                "IndividualSubjectScore_" + ((studyId != null) ? (studyId) : (sessionId)),
                IndividualSubjectScoreExport.class, eventLogExports, Arrays.asList(fieldOrder));
    }

    public ExportFile exportTaskScoreSummaryFiles(Long studyId, Long sessionId, String path) {

        List<Long> sessionList = getSessionIds(studyId, sessionId);

        List<CompletedTaskScoreExport> eventLogExports = exportDao.getCompletedTaskScoreExportInfo(sessionList);
        List<CompletedTaskScoreSubjectTeamExport> eventLogsTeamExport = new ArrayList<>();
        for (CompletedTaskScoreExport ctse : eventLogExports) {
            CompletedTaskScoreSubjectTeamExport ctsste = new CompletedTaskScoreSubjectTeamExport(ctse.getStudyPrefix(),
                    ctse.getSessionSuffix(),
                    ctse.getSessionStartDate(),
                    ctse.getSessionId(),
                    ctse.getTaskName(),
                    ctse.getSoloTask(),
                    ctse.getTotalScore(),
                    ctse.getNumberOfEntries(),
                    ctse.getNumberOfProcesseEntries(),
                    ctse.getNumberOfRightAnswers(),
                    ctse.getNumberOfWrongAnswers());
            if (ctsste.getSoloTask()) {
                Subject su = subjectDao.get(ctse.getSoloSubject());
                ctsste.setSoloSubject(su.getSubjectExternalId());
                ctsste.setTeamSubjects("");
            } else {
                Team team = teamDao.get(ctse.getTeamSubjects());
                List<Subject> subjects = teamService.getSubjectsFromTeam(team);
                String sub = "";
                for (Subject su : subjects) {
                    sub += su.getSubjectExternalId() + " ";
                }
                ctsste.setSoloSubject("");
                ctsste.setTeamSubjects(sub);
            }
            eventLogsTeamExport.add(ctsste);
        }
        String[] fieldOrder = {"studyPrefix",
                "sessionSuffix",
                "sessionStartDate",
                "sessionId",
                "taskName",
                "soloTask",
                "soloSubject",
                "teamSubjects",
                "totalScore",
                "numberOfEntries",
                "numberOfProcessedEntries",
                "numberOfRightAnswers",
                "numberOfWrongAnswers"};

        return ExportUtils.getExportFileForSimplePojo(path,
                "TaskScore_" + ((studyId != null) ? (studyId) : (sessionId)),
                CompletedTaskScoreSubjectTeamExport.class, eventLogsTeamExport, Arrays.asList(fieldOrder));
    }

    public List<ExportFile> exportTaskSnapshotSummaryFiles(Long studyId, Long sessionId, String path) {

        List<Long> sessionList = getSessionIds(studyId, sessionId);
        List<CompletedTask> completedTaskList = completedTaskDao.listBySessionId(sessionList);
        List<ExportFile> ret = new ArrayList<>();
        Map<Long, List<CompletedTask>> groupedByTask = new HashMap<>();
        //System.out.println(" Extracting around : " + completedTaskList.size());
        for (CompletedTask ct : completedTaskList) {
            List<CompletedTask> currentList = groupedByTask.get(ct.getTaskId());
            if (currentList == null) {
                currentList = new ArrayList<>();
            }
            currentList.add(ct);
            groupedByTask.put(ct.getTaskId(), currentList);
        }
        for (Long taskId : groupedByTask.keySet()) {
            List<CompletedTask> completedTasks = groupedByTask.get(taskId);

            ret.add(generateTaskSnapshotFile(completedTasks, taskId, path));
        }

        return ret;
    }

    private ExportFile generateTaskSnapshotFile(List<CompletedTask> completedTasks, Long taskId, String tempdir) {

        Map<Long, List<CompletedTask>> groupedByTask = new HashMap<>();

        List<String> entries = new ArrayList<>();
        String header = null;
        Task task = taskDao.get(taskId);
        StringBuffer records = new StringBuffer();
        String fixedHeader = "Study prefix; Session name;Session id;Session start date; Task Name;Is solo;Team;Subject;";
        for (CompletedTask ct : completedTasks) {
            if(ct.getTaskId()== 300 ) continue;
            TaskSnapshotExportRunner csr = (TaskSnapshotExportRunner) context.getBean("taskSnapshotExportRunner");
            Round round = roundDao.get(ct.getRoundId());
            Session session = sessionDao.get(round.getSessionId());
            Study study = studyDao.get(session.getStudyId());
            //ct.getTeamId();
            String subject = "";
            if (task.getSoloTask()) {
                //System.out.println(ct.getSubjectId() + " --- task id" + task.getId() + " - "+ ct.getTaskId());

                if(ct.getSubjectId() == null){
                    //System.out.println(" CT with solo set and null: " + ct.getId());
                    continue;
                } else {
                    //System.out.println(">" +ct.getSubjectId() + "<");

                    Subject sub = subjectDao.get(ct.getSubjectId());

                    subject = sub.getSubjectExternalId();
                }

            }
            String rec = study.getStudySessionPrefix() + ";" + session.getSessionSuffix() + ";" +
                    session.getId() + ";" +
                    session.getSessionStartDate() + ";" +
                    task.getTaskName() + ";" + task.getSoloTask() + ";" +
                    ct.getTeamId() + ";" + subject + ";";

            csr.setSession(new SessionWrapper(session));
            csr.setTaskWrapper(new TaskWrapper(task));
            csr.setCompletedTask(ct);
            csr.run();
            List<String> recordLines = csr.getExportLines();
            if (recordLines != null) {
                for (String line : recordLines)
                    entries.add(rec + line);
            }
            if (header == null) {
                header = fixedHeader + csr.getHeaderColumns();
            }
        }
        for (String en : entries) {
            records.append(en + "\n");
        }

        ExportFile ef = new ExportFile();
        ef.setFileName(task.getTaskName() + ".csv");
        ef.setFileRootPath(tempdir);
        ef.setFileHeader(header);
        ef.setFileType("TaskExport");
        ef.setFileContent(records.toString());

        return ef;

    }

    public ExportFile exportStudySubjectSummary(Long studyId, String path) {

        //get all sessions for study
        List<Session> sessionList = sessionDao.listByStudyId(studyId);
        Map<Long, Session> sessionMap = new HashMap<>();
        List<Long> sessionIdList = new ArrayList<>();
        sessionList.forEach(session -> {
            sessionIdList.add(session.getId());
            sessionMap.put(session.getId(), session);
        });
        List<Subject> subjects = subjectDao.listBySessionList(sessionIdList);
        Map<Long, SubjectStudyScore> subjectStudyScores = new HashMap<>();
        for (Subject su : subjects) {
            SubjectStudyScore sss = new SubjectStudyScore();
            sss.setSubject(su);
            SessionRelatedScore srs = new SessionRelatedScore();
            srs.setSubject(su);
            srs.setSession(sessionMap.get(su.getSessionId()));
            sss.setRelatedScoreList(new ArrayList<>());
            sss.getRelatedScoreList().add(srs);
            subjectStudyScores.put(sss.getSubject().getId(), sss);
        }

        Map<Long, SubjectStudyScore> subjectListGrouped = groupSubjectsByPreviousSubjectId(
                sessionMap, subjectStudyScores);


        Map<Long, List<Task>> taskListBySessionId = new HashMap<>();

        for (SubjectStudyScore sss : subjectListGrouped.values()) {
            List<SubjectAttribute> attributes = subjectService.getSubjectAttributes(sss.getSubject().getId());
            sss.setSubjectAttribute(attributes);
            for (SessionRelatedScore srs : sss.getRelatedScoreList()) {
                List<Task> taskList = new ArrayList<>();
                Long sessionId = (srs.getSession().getParentSessionId() != null) ?
                        (srs.getSession().getParentSessionId()) : (srs.getSession().getId());
                if (taskListBySessionId.get(sessionId) == null) {
                    List<SessionHasTaskGroup> taskGroupList = sessionService.listSessionHasTaskGroupBySessionId(sessionId);
                    for (SessionHasTaskGroup sshtg : taskGroupList) {
                        List<TaskGroupHasTask> tghtList = taskGroupService.listTaskGroupHasTaskByTaskGroup(sshtg.getTaskGroupId());
                        for (TaskGroupHasTask tght : tghtList) {
                            Task task = taskDao.get(tght.getTaskId());
                            taskList.add(task);
                        }
                    }
                    taskListBySessionId.put(sessionId, taskList);
                }

                List<Task> tasks = taskListBySessionId.get(sessionId);
                if (tasks != null) {
                    srs.setTaskNames(new ArrayList<>());
                    srs.setTaskIndividualScores(new ArrayList<>());
                    srs.setTaskGroupScores(new ArrayList<>());

                    for (Task t : tasks) {
                        if (t.getShouldScore()) {
                            srs.getTaskNames().add(t.getTaskName());
                            if (t.getSoloTask()) {
                                CompletedTask ct = completedTaskDao.getBySubjectIdTaskId(srs.getSubject().getId(), t.getId());
                                if (ct != null) {
                                    CompletedTaskScore cts = completedTaskScoreDao.getByCompletedTaskId(ct.getId());

                                    if (cts != null) {
                                        srs.getTaskGroupScores().add(cts.getTotalScore());
                                        srs.getTaskIndividualScores().add(cts.getTotalScore());
                                    } else {
                                        srs.getTaskGroupScores().add(0.0);
                                        srs.getTaskIndividualScores().add(0.0);
                                    }
                                }
                            } else {

                                List<Round> roundList = roundDao.listBySessionId(srs.getSession().getId());
                                if (roundList != null && roundList.size() > 0) {
                                    Round round = roundList.get(0);

                                    Team team = teamDao.getSubjectTeam(srs.getSubject().getId(),
                                            srs.getSession().getId(), round.getId(), null);

                                    if(team == null ){
                                        team = teamDao.getSubjectTeam(null,
                                                srs.getSession().getId(), round.getId(), t.getId());
                                    }
                                    if (team != null) {

                                        CompletedTask ct = completedTaskDao.getByRoundIdTaskIdTeamId(
                                                round.getId(),
                                                team.getId(), t.getId());

                                        CompletedTaskScore cts = completedTaskScoreDao.getByCompletedTaskId(ct.getId());
                                        if (cts != null) {
                                            srs.getTaskGroupScores().add(cts.getTotalScore());
                                        } else {
                                            srs.getTaskGroupScores().add(0.0);
                                        }
                                        IndividualSubjectScore iss =
                                                individualSubjectScoreDao.getByGiven(srs.getSubject().getId(), ct.getId());
                                        if (iss != null) {
                                            srs.getTaskIndividualScores().add(iss.getIndividualScore());
                                        } else {

                                            srs.getTaskIndividualScores().add(0.0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        List<SubjectStudyScore> allSSS = new ArrayList<>();
        subjectListGrouped.values().forEach( subjectStudyScore -> allSSS.add(subjectStudyScore));

        String[] fieldOrder = {
                "workerId"
        };

        Map<Integer, Integer> mapOfNumberDepth = new HashMap<>();
        for(SubjectStudyScore sss: allSSS) {
            Integer currCount = mapOfNumberDepth.get(sss.getRelatedScoreList().size());
            if (currCount == null) {
                currCount = 0;
            }
            mapOfNumberDepth.put(sss.getRelatedScoreList().size(), currCount + 1);
        }
        Integer mostFrequentNumberDepth = 0;
        for (Integer value : mapOfNumberDepth.values()) {
            if(value > mostFrequentNumberDepth){
                mostFrequentNumberDepth = value;
            }
        }
        Integer chosenDepth = 0;
        for(Integer number : mapOfNumberDepth.keySet()){
            if(mostFrequentNumberDepth == mapOfNumberDepth.get(number)){
                chosenDepth = number;
            }
        }
        List<SubjectStudyScore> finalSSS = new ArrayList<>();
        for(SubjectStudyScore sss: allSSS) {
            if(chosenDepth == sss.getRelatedScoreList().size()) {
                finalSSS.add(sss);
            }
        }

        List<String> extraColumns = new ArrayList<>();

        if(finalSSS.get(0).getRelatedScoreList() != null &&
                finalSSS.get(0).getRelatedScoreList().size() > 0){

            for(int i =0; i < finalSSS.get(0).getRelatedScoreList().size(); i++){
                SessionRelatedScore ssz =finalSSS.get(0).getRelatedScoreList().get(i);
                extraColumns.add("#"+ (i+1) + " session id");
                extraColumns.add("#"+ (i+1) + " session name");
                extraColumns.add("#"+ (i+1) + " session start date");
                for(int j=0; j < ssz.getTaskNames().size(); j ++){
                    extraColumns.add("#"+ (j+1) + " task - " + ssz.getTaskNames().get(j) + " individual score");
                    extraColumns.add("#"+ (j+1) + " task - " + ssz.getTaskNames().get(j) + " group score");
                }
            }
        }

        return ExportUtils.getExportFileForSimplePojoWithExtraColumns(path,
                "SubjectScoreStudySummary" + ((studyId)),
                SubjectStudyScore.class, finalSSS, Arrays.asList(fieldOrder), extraColumns);

    }

    private static Map<Long, SubjectStudyScore> groupSubjectsByPreviousSubjectId(Map<Long, Session> sessionMap, Map<Long, SubjectStudyScore> subjectStudyScores) {
        Map<Long, SubjectStudyScore> firstSessionSubjects = new HashMap<>();
        Map<Long, SubjectStudyScore> nonFirstSessionSubjects = new HashMap<>();

        for (SubjectStudyScore sss : subjectStudyScores.values()) {
            if (sss.getSubject().getPreviousSessionSubject() == null) {
                firstSessionSubjects.put(sss.getSubject().getId(), sss);
            } else {
                nonFirstSessionSubjects.put(sss.getSubject().getId(), sss);
            }
        }
        //resolveMainSubject
        for (SubjectStudyScore sss : nonFirstSessionSubjects.values()) {
            SubjectStudyScore ssu = resolveMainSubject(sss, subjectStudyScores);
            if (ssu != null) {
                //ADD TO original subject list.
                if (firstSessionSubjects.get(ssu.getSubject().getId()) != null) {
                    if(sss.getRelatedScoreList() != null && sss.getRelatedScoreList().size() > 0) {
                        firstSessionSubjects.get(ssu.getSubject().getId())
                                .getRelatedScoreList().add(sss.getRelatedScoreList().get(0));
                    }
                }
            }
        }
        return firstSessionSubjects;
    }

    private static SubjectStudyScore resolveMainSubject(SubjectStudyScore sss,
                                                        Map<Long, SubjectStudyScore> subjectStudyScores) {

        if (subjectStudyScores.get(sss.getSubject().getPreviousSessionSubject()) != null) {
            SubjectStudyScore ssOrigin = subjectStudyScores.get(
                    sss.getSubject().getPreviousSessionSubject());

            if (ssOrigin.getSubject().getPreviousSessionSubject() == null) {
                return ssOrigin;
            } else {
                return resolveMainSubject(ssOrigin, subjectStudyScores);

            }
        } else {
            return null;
        }
    }


}
