package edu.mit.cci.pogs.service.summaryexport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.export.CompletedTaskScoreExport;
import edu.mit.cci.pogs.model.dao.export.CompletedTaskScoreSubjectTeamExport;
import edu.mit.cci.pogs.model.dao.export.EventLogExport;
import edu.mit.cci.pogs.model.dao.export.ExportDao;
import edu.mit.cci.pogs.model.dao.export.SubjectExport;
import edu.mit.cci.pogs.model.dao.round.RoundDao;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.session.SessionScheduleType;
import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.runner.TaskAfterWorkRunner;
import edu.mit.cci.pogs.runner.TaskSnapshotExportRunner;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.service.CompletedTaskService;
import edu.mit.cci.pogs.service.SubjectService;
import edu.mit.cci.pogs.service.TeamService;
import edu.mit.cci.pogs.service.export.exportBeans.ExportFile;
import edu.mit.cci.pogs.utils.ExportUtils;

@Service
public class SummaryExportService {

    @Autowired
    private ExportDao exportDao;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private SubjectDao subjectdao;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private StudyDao studyDao;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private RoundDao roundDao;

    @Autowired
    private TaskDao taskDao;


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
            sessionList.add(session.getId());
        }
        return sessionList;
    }

    public ExportFile exportEventLog(Long studyId, Long sessionId, String path) {

        List<Long> sessionList = getSessionIds(studyId, sessionId);

        List<EventLogExport> eventLogExports = exportDao.getEventLogExportInfo(sessionList);
        for (EventLogExport ele : eventLogExports) {
            ele.setEventContent("");
            ele.setSummaryDescription(validateSummary(ele.getSummaryDescription()));
            if(ele.getReceiverId()!=null){
                Subject su = subjectdao.get(ele.getReceiverId());
                ele.setReceiverSubjectExternalId(su.getSubjectExternalId());
            } else {
                ele.setReceiverSubjectExternalId("");
            }
            
        }

        String[] fieldOrder = {
                "studyPrefix",
                "sessionSuffix",
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
            List<Subject> subjects = subjectdao.listBySessionIdOrParentSessionId(sessionIdz);
            Session session = sessionDao.get(sessionIdz);
            if (studyId == null) {
                study = studyDao.get(session.getStudyId());
            }
            for (Subject su : subjects) {
                List<SubjectAttribute> subjectAttributes = subjectService.getSubjectAttributes(su.getId());
                SubjectExport se = new SubjectExport(su);
                se.setStudyPrefix(study.getStudySessionPrefix());
                se.setSessionSuffix(session.getSessionSuffix());

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
                "subjectId",
                "subjectExternalId",
                "subjectDisplayName",
                "subjectPreviousSessionSubjectId"
        };


        return ExportUtils.getExportFileForSimplePojoWithExtraColumns(
                path,
                "Subjects_" + ((studyId != null) ? ("study[" + studyId + "]") : ("session[" + sessionId + "]")),
                SubjectExport.class,
                exports,
                Arrays.asList(fieldOrder),
                list);


    }

    public ExportFile exportTaskScoreSummaryFiles(Long studyId, Long sessionId, String path) {

        List<Long> sessionList = getSessionIds(studyId, sessionId);

        List<CompletedTaskScoreExport> eventLogExports = exportDao.getCompletedTaskScoreExportInfo(sessionList);
        List<CompletedTaskScoreSubjectTeamExport> eventLogsTeamExport = new ArrayList<>();
        for (CompletedTaskScoreExport ctse : eventLogExports) {
            CompletedTaskScoreSubjectTeamExport ctsste = new CompletedTaskScoreSubjectTeamExport(ctse.getStudyPrefix(),
                    ctse.getSessionSuffix(),
                    ctse.getTaskName(),
                    ctse.getSoloTask(),
                    ctse.getTotalScore(),
                    ctse.getNumberOfEntries(),
                    ctse.getNumberOfProcesseEntries(),
                    ctse.getNumberOfRightAnswers(),
                    ctse.getNumberOfWrongAnswers());
            if (ctsste.getSoloTask()) {
                Subject su = subjectdao.get(ctse.getSoloSubject());
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
        String fixedHeader = "Study prefix; Session name; Session id; Task Name;Is solo;Team;Subject;";
        for (CompletedTask ct : completedTasks) {
            TaskSnapshotExportRunner csr = (TaskSnapshotExportRunner) context.getBean("taskSnapshotExportRunner");
            Round round = roundDao.get(ct.getRoundId());
            Session session = sessionDao.get(round.getSessionId());
            Study study = studyDao.get(session.getStudyId());
            ct.getTeamId();
            String subject = "";
            if(task.getSoloTask()){
                Subject sub = subjectdao.get(ct.getSubjectId());
                subject = sub.getSubjectExternalId();
            }
            String rec = study.getStudySessionPrefix() + ";" + session.getSessionSuffix() + ";" +
                    session.getId() + ";" + task.getTaskName() + ";" + task.getSoloTask() + ";" +
                    ct.getTeamId() + ";" +  subject + ";";

            csr.setSession(new SessionWrapper(session));
            csr.setTaskWrapper(new TaskWrapper(task));
            csr.setCompletedTask(ct);
            csr.run();
            List<String> recordLines = csr.getExportLines();
            if (recordLines != null) {
                for(String line: recordLines)
                entries.add( rec + line);
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
}
