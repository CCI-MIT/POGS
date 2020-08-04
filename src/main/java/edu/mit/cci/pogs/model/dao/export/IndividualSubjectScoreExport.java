package edu.mit.cci.pogs.model.dao.export;

import java.sql.Timestamp;

public class IndividualSubjectScoreExport {
    private String studyPrefix;
    private String sessionSuffix;
    private Timestamp sessionStartDate;
    private Long sessionId;
    private String taskName;
    private Boolean soloTask;
    private Long subjectId;
    private String subjectExternalId;
    private Double totalScore;

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

    public Boolean getSoloTask() {
        return soloTask;
    }

    public void setSoloTask(Boolean soloTask) {
        this.soloTask = soloTask;
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

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Timestamp getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(Timestamp sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
