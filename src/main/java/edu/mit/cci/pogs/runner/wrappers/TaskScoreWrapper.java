package edu.mit.cci.pogs.runner.wrappers;

import java.util.List;

public class TaskScoreWrapper {

    private List<Double> teamScore;

    private TaskWrapper taskWrapper;


    public List<Double> getTeamScore() {
        return teamScore;
    }

    public void setTeamScore(List<Double> teamScore) {
        this.teamScore = teamScore;
    }

    public TaskWrapper getTaskWrapper() {
        return taskWrapper;
    }

    public void setTaskWrapper(TaskWrapper taskWrapper) {
        this.taskWrapper = taskWrapper;
    }
}
