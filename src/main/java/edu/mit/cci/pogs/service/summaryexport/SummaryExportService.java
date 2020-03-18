package edu.mit.cci.pogs.service.summaryexport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.cci.pogs.model.dao.export.CompletedTaskScoreExport;
import edu.mit.cci.pogs.model.dao.export.CompletedTaskScoreSubjectTeamExport;
import edu.mit.cci.pogs.model.dao.export.EventLogExport;
import edu.mit.cci.pogs.model.dao.export.ExportDao;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
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


    private List<Long> getSessionIds(Long studyId, Long sessionId){
        List<Long> sessionList = new ArrayList<>();
        if (studyId != null) {
            for (Session su : sessionDao.listByStudyId(studyId)) {
                sessionList.add(su.getId());
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
        for(EventLogExport ele : eventLogExports){
            ele.setEventContent("");
            ele.setSummaryDescription(validateSummary(ele.getSummaryDescription()));
        }

        String[] fieldOrder = {
                "studyPrefix",
                "sessionSuffix",
                "taskName",
                "soloTask",
                "eventType",
                "eventContent",
                "timestamp",
                "subjectExternalId",
                "summaryDescription"
        };

        return ExportUtils.getExportFileForSimplePojo(path, "EventLog_" +
                        ((studyId!=null)?(studyId):(sessionId)), EventLogExport.class, eventLogExports,
                Arrays.asList(fieldOrder));
    }


    private static String validateSummary(String summaryDescription) {
        if(summaryDescription == null) return "";
        summaryDescription = summaryDescription.replaceAll("\\n", "¶");
        summaryDescription = summaryDescription.replaceAll(",", "|");
        summaryDescription = summaryDescription.replaceAll(";", "|");
        return  summaryDescription;
    }

    public List<ExportFile> exportTaskSummaryFiles(Long studyId, Long sessionId, String path) {
        return null;
    }
    public ExportFile exportTaskScoreSummaryFiles(Long studyId, Long sessionId, String path) {

        List<Long> sessionList = getSessionIds(studyId, sessionId);

        List<CompletedTaskScoreExport> eventLogExports = exportDao.getCompletedTaskScoreExportInfo(sessionList);
        List<CompletedTaskScoreSubjectTeamExport> eventLogsTeamExport = new ArrayList<>();
        for(CompletedTaskScoreExport ctse: eventLogExports){
            CompletedTaskScoreSubjectTeamExport ctsste =new CompletedTaskScoreSubjectTeamExport(ctse.getStudyPrefix(),
                    ctse.getSessionSuffix(),
                    ctse.getTaskName(),
                    ctse.getSoloTask(),
                    ctse.getTotalScore(),
                    ctse.getNumberOfEntries(),
                    ctse.getNumberOfProcesseEntries(),
                    ctse.getNumberOfRightAnswers(),
                    ctse.getNumberOfWrongAnswers());
            if(ctsste.getSoloTask()){
                Subject su = subjectdao.get(ctse.getSoloSubject());
                ctsste.setSoloSubject(su.getSubjectExternalId());
                ctsste.setTeamSubjects("");
            } else {
                Team team = teamDao.get(ctse.getTeamSubjects());
                List<Subject> subjects = teamService.getSubjectsFromTeam(team);
                String sub = "";
                for(Subject su: subjects){
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
                "TaskScore_" + ((studyId!=null)?(studyId):(sessionId)),
                CompletedTaskScoreSubjectTeamExport.class, eventLogsTeamExport, Arrays.asList(fieldOrder));
    }
}
