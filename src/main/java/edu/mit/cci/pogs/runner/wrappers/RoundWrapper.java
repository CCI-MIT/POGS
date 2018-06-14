package edu.mit.cci.pogs.runner.wrappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;

public class RoundWrapper extends Round {

    private Long roundStartTimestamp;

    public RoundWrapper(Round value) {
        super(value);
        tasks = new ArrayList<>();
        roundTeams = new ArrayList<>();
    }

    private List<TaskWrapper> tasks;

    private Integer currentTaskIndex;

    private List<TeamWrapper> roundTeams;

    private TaskWrapper currentTask;

    public List<TaskWrapper> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskWrapper> tasks) {
        this.tasks = tasks;
    }

    public Integer getCurrentTaskIndex() {
        return currentTaskIndex;
    }

    public void setCurrentTaskIndex(Integer currentTaskIndex) {
        this.currentTaskIndex = currentTaskIndex;
    }

    public List<TeamWrapper> getRoundTeams() {
        return roundTeams;
    }

    public void setRoundTeams(List<TeamWrapper> roundTeams) {
        this.roundTeams = roundTeams;
    }

    public TaskWrapper getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(TaskWrapper currentTask) {
        this.currentTask = currentTask;
    }

    public Long getRoundStartTimestamp() {
        return roundStartTimestamp;
    }

    public void setRoundStartTimestamp(Long roundStartTimestamp) {
        this.roundStartTimestamp = roundStartTimestamp;
    }

    public Long getTotalRoundTime() {
        Long total = 0l;
        for (TaskWrapper tw : tasks) {
            total += tw.getTotalTaskTime();
        }
        return total;
    }

    public Long getRoundFinishTimestamp() {
        return getRoundStartTimestamp() + getTotalRoundTime();
    }

    public boolean isHappeningNow(Long currentTimestamp) {
        return currentTimestamp >= roundStartTimestamp && currentTimestamp <= getRoundFinishTimestamp();
    }

    public TaskWrapper getCurrentTask(Long nowTimestamp) {
        if (isHappeningNow(nowTimestamp)) {
            for (TaskWrapper tw : tasks) {
                if (tw.isHappeningNow(nowTimestamp)) {
                    return tw;
                }
            }
        }
        return null;
    }

    public TaskWrapper getNextTask() {
        for (int i = 0; i < tasks.size(); i++) {
            TaskWrapper tw = tasks.get(i);
            if (tw.isHappeningNow(new Date().getTime())) {
                if (i + 1 != tasks.size()) {
                    return tw;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public String getUrl() {
        return "/round/"+this.getId();
    }
}
