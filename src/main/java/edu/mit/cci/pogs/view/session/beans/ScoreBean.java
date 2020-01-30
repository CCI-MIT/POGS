package edu.mit.cci.pogs.view.session.beans;


public class ScoreBean {


    private Long id;

    private Long taskId;
    private String taskName;
    private Double  totalScore;
    private Long    completedTaskId;
    private Integer numberOfRightAnswers;
    private Integer numberOfWrongAnswers;
    private Integer numberOfEntries;
    private Integer numberOfProcessedEntries;
    private String  scoringData;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Long getCompletedTaskId() {
        return completedTaskId;
    }

    public void setCompletedTaskId(Long completedTaskId) {
        this.completedTaskId = completedTaskId;
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

    public Integer getNumberOfEntries() {
        return numberOfEntries;
    }

    public void setNumberOfEntries(Integer numberOfEntries) {
        this.numberOfEntries = numberOfEntries;
    }

    public Integer getNumberOfProcessedEntries() {
        return numberOfProcessedEntries;
    }

    public void setNumberOfProcessedEntries(Integer numberOfProcessedEntries) {
        this.numberOfProcessedEntries = numberOfProcessedEntries;
    }

    public String getScoringData() {
        return scoringData;
    }

    public void setScoringData(String scoringData) {
        this.scoringData = scoringData;
    }
}
