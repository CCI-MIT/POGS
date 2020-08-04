package edu.mit.cci.pogs.model.dao.export;

import java.sql.Timestamp;

public class EventLogExport  {

    private String studyPrefix;
    private String sessionSuffix;
    private Timestamp sessionStartDate;
    private Long sessionId;
    private String senderSubjectExternalId;
    private String receiverSubjectExternalId;
    private Long receiverId;
    private String taskName;
    private Boolean soloTask;
    private Timestamp timestamp;
    private String eventType;
    private String eventContent;
    private String summaryDescription;

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

    public String getSenderSubjectExternalId() {
        return senderSubjectExternalId;
    }

    public void setSenderSubjectExternalId(String senderSubjectExternalId) {
        this.senderSubjectExternalId = senderSubjectExternalId;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventContent() {
        return eventContent;
    }

    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }

    public String getSummaryDescription() {
        return summaryDescription;
    }

    public void setSummaryDescription(String summaryDescription) {
        this.summaryDescription = summaryDescription;
    }

    public String getReceiverSubjectExternalId() {
        return receiverSubjectExternalId;
    }

    public void setReceiverSubjectExternalId(String receiverSubjectExternalId) {
        this.receiverSubjectExternalId = receiverSubjectExternalId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
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
