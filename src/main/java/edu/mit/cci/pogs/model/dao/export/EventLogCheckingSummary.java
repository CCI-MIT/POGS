package edu.mit.cci.pogs.model.dao.export;

import java.sql.Timestamp;
import java.util.List;

public class EventLogCheckingSummary {

    private String studyPrefix;
    private String sessionSuffix;
    private String taskName;
    private Timestamp sessionStartDate;
    private Long teamId;
    private Long completedTaskId;
    private String subjectExternalId;
    private Long subjectId;
    private Long subjectCount;
    private Timestamp timestamp;
    private List<String> subjectsNames;
    private List<String> subjectsPingCount;
    private String taskPingStandardDeviation;


    public String getStudyPrefix() {
        return studyPrefix;
    }

    public void setStudyPrefix(String studyPrefix) {
        this.studyPrefix = studyPrefix;
    }

    public String getSessionSuffix() {
        return sessionSuffix;
    }

    public void setSessionSuffix(String sessionSuffix) {
        this.sessionSuffix = sessionSuffix;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getSubjectCount() {
        return subjectCount;
    }

    public void setSubjectCount(Long subjectCount) {
        this.subjectCount = subjectCount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getSubjectsNamesStr() {
        String ret = "[";
        for(int i =0; i < this.subjectsNames.size(); i ++){
            ret += this.subjectsNames.get(i);
            if(i + 1 < this.subjectsNames.size()){
                ret += ",";
            }
        }
        ret += "]";
        return ret;
    }

    public String getSubjectsPingCountStr() {
        String ret = "[";
        for(int i =0; i < this.subjectsPingCount.size(); i ++){
            ret += this.subjectsPingCount.get(i);
            if(i + 1 < this.subjectsPingCount.size()){
                ret += ",";
            }
        }
        ret += "]";
        return ret;
    }

    public List<String> getSubjectsNames() {
        return subjectsNames;
    }

    public void setSubjectsNames(List<String> subjectsNames) {
        this.subjectsNames = subjectsNames;
    }

    public List<String> getSubjectsPingCount() {
        return subjectsPingCount;
    }

    public void setSubjectsPingCount(List<String> subjectsPingCount) {
        this.subjectsPingCount = subjectsPingCount;
    }

    public String getTaskPingStandardDeviation() {
        return taskPingStandardDeviation;
    }

    public void setTaskPingStandardDeviation(String taskPingStandardDeviation) {
        this.taskPingStandardDeviation = taskPingStandardDeviation;
    }

    public Long getCompletedTaskId() {
        return completedTaskId;
    }

    public void setCompletedTaskId(Long completedTaskId) {
        this.completedTaskId = completedTaskId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectExternalId() {
        return subjectExternalId;
    }

    public void setSubjectExternalId(String subjectExternalId) {
        this.subjectExternalId = subjectExternalId;
    }

    public Timestamp getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(Timestamp sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}
