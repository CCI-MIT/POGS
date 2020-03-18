package edu.mit.cci.pogs.model.dao.export;

public class CompletedTaskScoreExport {
    private String studyPrefix;
    private String sessionSuffix;
    private String taskName;
    private Boolean soloTask;
    private Long soloSubject;
    private Long teamSubjects;
    private Double totalScore;
    private Integer numberOfEntries;
    private Integer numberOfProcesseEntries;
    private Integer numberOfRightAnswers;
    private Integer numberOfWrongAnswers;

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

    public Long getSoloSubject() {
        return soloSubject;
    }

    public void setSoloSubject(Long soloSubject) {
        this.soloSubject = soloSubject;
    }

    public Long getTeamSubjects() {
        return teamSubjects;
    }

    public void setTeamSubjects(Long teamSubjects) {
        this.teamSubjects = teamSubjects;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getNumberOfEntries() {
        return numberOfEntries;
    }

    public void setNumberOfEntries(Integer numberOfEntries) {
        this.numberOfEntries = numberOfEntries;
    }

    public Integer getNumberOfProcesseEntries() {
        return numberOfProcesseEntries;
    }

    public void setNumberOfProcesseEntries(Integer numberOfProcesseEntries) {
        this.numberOfProcesseEntries = numberOfProcesseEntries;
    }

    public Integer getNumberOfRightAnswers() {
        return numberOfRightAnswers;
    }

    public void setNumberOfRightAnswers(Integer numberOfRightAnswers) {
        this.numberOfRightAnswers = numberOfRightAnswers;
    }

    public Integer getNumberOfWrongAnswers() {
        return numberOfWrongAnswers;
    }

    public void setNumberOfWrongAnswers(Integer numberOfWrongAnswers) {
        this.numberOfWrongAnswers = numberOfWrongAnswers;
    }
}
