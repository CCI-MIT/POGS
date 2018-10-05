package edu.mit.cci.pogs.view.task.bean;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;

public class TaskBean {

    private Long id;
    private String taskName;
    private String taskPluginType;
    private Boolean soloTask;
    private Integer interactionTime;
    private Boolean introPageEnabled;
    private String introText;
    private Integer introTime;
    private Boolean primerPageEnabled;
    private String primerText;
    private Integer primerTime;
    private Boolean primerVideoAutoplayMute;
    private Boolean interactionWidgetEnabled;
    private String interactionText;
    private String communicationType;
    private Boolean collaborationTodoListEnabled;
    private Boolean collaborationFeedbackWidgetEnabled;
    private Boolean collaborationVotingWidgetEnabled;
    private String scoringType;
    private Long subjectCommunicationId;
    private Long chatScriptId;

    private Long taskConfigurationId;
    private Boolean shouldScore;



    private ResearchGroupRelationshipBean researchGroupRelationshipBean;

    public TaskBean() {
    }

    public TaskBean(Task value) {
        this.id = value.getId();
        this.taskName = value.getTaskName();
        this.taskPluginType = value.getTaskPluginType();
        this.soloTask = value.getSoloTask();
        this.interactionTime = value.getInteractionTime();
        this.introPageEnabled = value.getIntroPageEnabled();
        this.introText = value.getIntroText();
        this.introTime = value.getIntroTime();
        this.primerPageEnabled = value.getPrimerPageEnabled();
        this.primerText = value.getPrimerText();
        this.primerTime = value.getPrimerTime();
        this.interactionWidgetEnabled = value.getInteractionWidgetEnabled();
        this.interactionText = value.getInteractionText();
        this.communicationType = value.getCommunicationType();
        this.collaborationTodoListEnabled = value.getCollaborationTodoListEnabled();
        this.collaborationFeedbackWidgetEnabled = value.getCollaborationFeedbackWidgetEnabled();
        this.collaborationVotingWidgetEnabled = value.getCollaborationVotingWidgetEnabled();
        this.scoringType = value.getScoringType();
        this.subjectCommunicationId = value.getSubjectCommunicationId();
        this.chatScriptId = value.getChatScriptId();
        this.primerVideoAutoplayMute = value.getPrimerVideoAutoplayMute();
    }

    public ResearchGroupRelationshipBean getResearchGroupRelationshipBean() {
        return researchGroupRelationshipBean;
    }

    public void setResearchGroupRelationshipBean(ResearchGroupRelationshipBean researchGroupRelationshipBean) {
        this.researchGroupRelationshipBean = researchGroupRelationshipBean;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskPluginType() {
        return taskPluginType;
    }

    public void setTaskPluginType(String taskPluginType) {
        this.taskPluginType = taskPluginType;
    }

    public Boolean getSoloTask() {
        return soloTask;
    }

    public void setSoloTask(Boolean soloTask) {
        this.soloTask = soloTask;
    }

    public Integer getInteractionTime() {
        return interactionTime;
    }

    public void setInteractionTime(Integer interactionTime) {
        this.interactionTime = interactionTime;
    }

    public Boolean getIntroPageEnabled() {
        return introPageEnabled;
    }

    public void setIntroPageEnabled(Boolean introPageEnabled) {
        this.introPageEnabled = introPageEnabled;
    }

    public String getIntroText() {
        return introText;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }

    public Integer getIntroTime() {
        return introTime;
    }

    public void setIntroTime(Integer introTime) {
        this.introTime = introTime;
    }

    public Boolean getPrimerPageEnabled() {
        return primerPageEnabled;
    }

    public void setPrimerPageEnabled(Boolean primerPageEnabled) {
        this.primerPageEnabled = primerPageEnabled;
    }

    public String getPrimerText() {
        return primerText;
    }

    public void setPrimerText(String primerText) {
        this.primerText = primerText;
    }

    public Integer getPrimerTime() {
        return primerTime;
    }

    public void setPrimerTime(Integer primerTime) {
        this.primerTime = primerTime;
    }

    public Boolean getInteractionWidgetEnabled() {
        return interactionWidgetEnabled;
    }

    public void setInteractionWidgetEnabled(Boolean interactionWidgetEnabled) {
        this.interactionWidgetEnabled = interactionWidgetEnabled;
    }

    public String getInteractionText() {
        return interactionText;
    }

    public void setInteractionText(String interactionText) {
        this.interactionText = interactionText;
    }

    public String getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType(String communicationType) {
        this.communicationType = communicationType;
    }

    public Boolean getCollaborationTodoListEnabled() {
        return collaborationTodoListEnabled;
    }

    public void setCollaborationTodoListEnabled(Boolean collaborationTodoListEnabled) {
        this.collaborationTodoListEnabled = collaborationTodoListEnabled;
    }

    public Boolean getCollaborationFeedbackWidgetEnabled() {
        return collaborationFeedbackWidgetEnabled;
    }

    public void setCollaborationFeedbackWidgetEnabled(Boolean collaborationFeedbackWidgetEnabled) {
        this.collaborationFeedbackWidgetEnabled = collaborationFeedbackWidgetEnabled;
    }

    public Boolean getCollaborationVotingWidgetEnabled() {
        return collaborationVotingWidgetEnabled;
    }

    public void setCollaborationVotingWidgetEnabled(Boolean collaborationVotingWidgetEnabled) {
        this.collaborationVotingWidgetEnabled = collaborationVotingWidgetEnabled;
    }

    public String getScoringType() {
        return scoringType;
    }

    public void setScoringType(String scoringType) {
        this.scoringType = scoringType;
    }

    public Long getSubjectCommunicationId() {
        return subjectCommunicationId;
    }

    public void setSubjectCommunicationId(Long subjectCommunicationId) {
        this.subjectCommunicationId = subjectCommunicationId;
    }

    public Long getTaskConfigurationId() {
        return taskConfigurationId;
    }

    public void setTaskConfigurationId(Long taskConfigurationId) {
        this.taskConfigurationId = taskConfigurationId;
    }

    public Long getChatScriptId() {
        return chatScriptId;
    }

    public void setChatScriptId(Long chatScriptId) {
        this.chatScriptId = chatScriptId;
    }

    public Boolean getPrimerVideoAutoplayMute() {
        return primerVideoAutoplayMute;
    }

    public void setPrimerVideoAutoplayMute(Boolean primerVideoAutoplayMute) {
        this.primerVideoAutoplayMute = primerVideoAutoplayMute;
    }

    public Boolean getShouldScore() {
        return shouldScore;
    }

    public void setShouldScore(Boolean shouldScore) {
        this.shouldScore = shouldScore;
    }
}
