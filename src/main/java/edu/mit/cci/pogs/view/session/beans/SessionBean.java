package edu.mit.cci.pogs.view.session.beans;

import java.sql.Timestamp;

import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;

public class SessionBean {

    private Long id;
    private String sessionSuffix;
    private Timestamp sessionStartDate;
    private Long conditionId;
    private String status;
    private Integer waitingRoomTime;
    private Boolean introPageEnabled;
    private String introText;
    private Integer introTime;
    private Boolean displayNameChangePageEnabled;
    private Integer displayNameChangeTime;
    private Boolean rosterPageEnabled;
    private Integer rosterTime;
    private Boolean donePageEnabled;
    private String donePageText;
    private Integer donePageTime;
    private String doneRedirectUrl;
    private String couldNotAssignToTeamMessage;
    private String taskExecutionType;
    private Boolean roundsEnabled;
    private Integer numberOfRounds;
    private String communicationType;
    private String chatBotName;
    private Boolean scoreboardEnabled;
    private String scoreboardDisplayType;
    private Boolean scoreboardUseDisplayNames;
    private Boolean collaborationTodoListEnabled;
    private Boolean collaborationFeedbackWidgetEnabled;
    private Boolean collaborationVotingWidgetEnabled;
    private String teamCreationMoment;
    private String teamCreationType;
    private Integer teamMinSize;
    private Integer teamMaxSize;
    private String teamCreationMethod;
    private String teamCreationMatrix;

    private SessionHasTaskGroupRelationshipBean sessionHasTaskGroupRelationshipBean;

    private Long studyId;


    public SessionBean() {

    }

    public SessionBean(Session value) {
        this.id = value.getId();
        this.sessionSuffix = value.getSessionSuffix();
        this.sessionStartDate = value.getSessionStartDate();
        this.conditionId = value.getConditionId();
        this.status = value.getStatus();
        this.waitingRoomTime = value.getWaitingRoomTime();
        this.introPageEnabled = value.getIntroPageEnabled();
        this.introText = value.getIntroText();
        this.introTime = value.getIntroTime();
        this.displayNameChangePageEnabled = value.getDisplayNameChangePageEnabled();
        this.displayNameChangeTime = value.getDisplayNameChangeTime();
        this.rosterPageEnabled = value.getRosterPageEnabled();
        this.rosterTime = value.getRosterTime();
        this.donePageEnabled = value.getDonePageEnabled();
        this.donePageText = value.getDonePageText();
        this.donePageTime = value.getDonePageTime();
        this.doneRedirectUrl = value.getDoneRedirectUrl();
        this.couldNotAssignToTeamMessage = value.getCouldNotAssignToTeamMessage();
        this.taskExecutionType = value.getTaskExecutionType();
        this.roundsEnabled = value.getRoundsEnabled();
        this.numberOfRounds = value.getNumberOfRounds();
        this.communicationType = value.getCommunicationType();
        this.chatBotName = value.getChatBotName();
        this.scoreboardEnabled = value.getScoreboardEnabled();
        this.scoreboardDisplayType = value.getScoreboardDisplayType();
        this.scoreboardUseDisplayNames = value.getScoreboardUseDisplayNames();
        this.collaborationTodoListEnabled = value.getCollaborationTodoListEnabled();
        this.collaborationFeedbackWidgetEnabled = value.getCollaborationFeedbackWidgetEnabled();
        this.collaborationVotingWidgetEnabled = value.getCollaborationVotingWidgetEnabled();
        this.teamCreationMoment = value.getTeamCreationMoment();
        this.teamCreationType = value.getTeamCreationType();
        this.teamMinSize = value.getTeamMinSize();
        this.teamMaxSize = value.getTeamMaxSize();
        this.teamCreationMethod = value.getTeamCreationMethod();
        this.teamCreationMatrix = value.getTeamCreationMatrix();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionSuffix() {
        return sessionSuffix;
    }

    public void setSessionSuffix(String sessionSuffix) {
        this.sessionSuffix = sessionSuffix;
    }

    public Timestamp getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(Timestamp sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getWaitingRoomTime() {
        return waitingRoomTime;
    }

    public void setWaitingRoomTime(Integer waitingRoomTime) {
        this.waitingRoomTime = waitingRoomTime;
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

    public Boolean getDisplayNameChangePageEnabled() {
        return displayNameChangePageEnabled;
    }

    public void setDisplayNameChangePageEnabled(Boolean displayNameChangePageEnabled) {
        this.displayNameChangePageEnabled = displayNameChangePageEnabled;
    }

    public Integer getDisplayNameChangeTime() {
        return displayNameChangeTime;
    }

    public void setDisplayNameChangeTime(Integer displayNameChangeTime) {
        this.displayNameChangeTime = displayNameChangeTime;
    }

    public Boolean getRosterPageEnabled() {
        return rosterPageEnabled;
    }

    public void setRosterPageEnabled(Boolean rosterPageEnabled) {
        this.rosterPageEnabled = rosterPageEnabled;
    }

    public Integer getRosterTime() {
        return rosterTime;
    }

    public void setRosterTime(Integer rosterTime) {
        this.rosterTime = rosterTime;
    }

    public Boolean getDonePageEnabled() {
        return donePageEnabled;
    }

    public void setDonePageEnabled(Boolean donePageEnabled) {
        this.donePageEnabled = donePageEnabled;
    }

    public String getDonePageText() {
        return donePageText;
    }

    public void setDonePageText(String donePageText) {
        this.donePageText = donePageText;
    }

    public Integer getDonePageTime() {
        return donePageTime;
    }

    public void setDonePageTime(Integer donePageTime) {
        this.donePageTime = donePageTime;
    }

    public String getDoneRedirectUrl() {
        return doneRedirectUrl;
    }

    public void setDoneRedirectUrl(String doneRedirectUrl) {
        this.doneRedirectUrl = doneRedirectUrl;
    }

    public String getCouldNotAssignToTeamMessage() {
        return couldNotAssignToTeamMessage;
    }

    public void setCouldNotAssignToTeamMessage(String couldNotAssignToTeamMessage) {
        this.couldNotAssignToTeamMessage = couldNotAssignToTeamMessage;
    }

    public String getTaskExecutionType() {
        return taskExecutionType;
    }

    public void setTaskExecutionType(String taskExecutionType) {
        this.taskExecutionType = taskExecutionType;
    }

    public Boolean getRoundsEnabled() {
        return roundsEnabled;
    }

    public void setRoundsEnabled(Boolean roundsEnabled) {
        this.roundsEnabled = roundsEnabled;
    }

    public Integer getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(Integer numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public String getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType(String communicationType) {
        this.communicationType = communicationType;
    }

    public String getChatBotName() {
        return chatBotName;
    }

    public void setChatBotName(String chatBotName) {
        this.chatBotName = chatBotName;
    }

    public Boolean getScoreboardEnabled() {
        return scoreboardEnabled;
    }

    public void setScoreboardEnabled(Boolean scoreboardEnabled) {
        this.scoreboardEnabled = scoreboardEnabled;
    }

    public String getScoreboardDisplayType() {
        return scoreboardDisplayType;
    }

    public void setScoreboardDisplayType(String scoreboardDisplayType) {
        this.scoreboardDisplayType = scoreboardDisplayType;
    }

    public Boolean getScoreboardUseDisplayNames() {
        return scoreboardUseDisplayNames;
    }

    public void setScoreboardUseDisplayNames(Boolean scoreboardUseDisplayNames) {
        this.scoreboardUseDisplayNames = scoreboardUseDisplayNames;
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

    public String getTeamCreationMoment() {
        return teamCreationMoment;
    }

    public void setTeamCreationMoment(String teamCreationMoment) {
        this.teamCreationMoment = teamCreationMoment;
    }

    public String getTeamCreationType() {
        return teamCreationType;
    }

    public void setTeamCreationType(String teamCreationType) {
        this.teamCreationType = teamCreationType;
    }

    public Integer getTeamMinSize() {
        return teamMinSize;
    }

    public void setTeamMinSize(Integer teamMinSize) {
        this.teamMinSize = teamMinSize;
    }

    public Integer getTeamMaxSize() {
        return teamMaxSize;
    }

    public void setTeamMaxSize(Integer teamMaxSize) {
        this.teamMaxSize = teamMaxSize;
    }

    public String getTeamCreationMethod() {
        return teamCreationMethod;
    }

    public void setTeamCreationMethod(String teamCreationMethod) {
        this.teamCreationMethod = teamCreationMethod;
    }

    public String getTeamCreationMatrix() {
        return teamCreationMatrix;
    }

    public void setTeamCreationMatrix(String teamCreationMatrix) {
        this.teamCreationMatrix = teamCreationMatrix;
    }

    public SessionHasTaskGroupRelationshipBean getSessionHasTaskGroupRelationshipBean() {
        return sessionHasTaskGroupRelationshipBean;
    }

    public void setSessionHasTaskGroupRelationshipBean(SessionHasTaskGroupRelationshipBean sessionHasTaskGroupRelationshipBean) {
        this.sessionHasTaskGroupRelationshipBean = sessionHasTaskGroupRelationshipBean;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public boolean getHasSessionStarted() {
        return !getStatus()
                .equals(SessionStatus.NOTSTARTED.getId().toString());
    }
}
