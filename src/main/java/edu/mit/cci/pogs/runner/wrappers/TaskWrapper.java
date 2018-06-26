package edu.mit.cci.pogs.runner.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.utils.DateUtils;

public class TaskWrapper extends Task {

    private Long taskStartTimestamp;

    private List<CompletedTask> completedTasks;

    private List<TeamWrapper> taskTeams;


    public TaskWrapper(Task value) {
        super(value);
        completedTasks = new ArrayList<>();
        taskTeams = new ArrayList<>();
    }


    public List<TeamWrapper> getTaskTeams() {
        return taskTeams;
    }

    public void setTaskTeams(List<TeamWrapper> taskTeams) {
        this.taskTeams = taskTeams;
    }

    public List<CompletedTask> getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(List<CompletedTask> completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Long getTaskStartTimestamp() {
        return taskStartTimestamp;
    }

    public void setTaskStartTimestamp(Long taskStartTimestamp) {
        this.taskStartTimestamp = taskStartTimestamp;
    }

    public Long getTotalTaskTime() {
        Long total = 0l;
        if (this.getIntroPageEnabled()) {
            total += DateUtils.toMilliseconds(this.getIntroTime());
        }
        if (this.getPrimerPageEnabled()) {
            total += DateUtils.toMilliseconds(this.getPrimerTime());
        }
        total += DateUtils.toMilliseconds(this.getInteractionTime());
        return total;
    }

    public Long getTaskEndTimestamp() {
        return getTaskStartTimestamp() + getTotalTaskTime();
    }

    public boolean isHappeningNow(Long currentTimestamp) {
        return currentTimestamp >= taskStartTimestamp && currentTimestamp <= getTaskEndTimestamp();
    }

    private Long getSecondsToStartTask() {
        return this.taskStartTimestamp - DateUtils.now();
    }
    //

    public String getNextUrl() {

        if (getSecondsToStartTask() > 0) {
            return "/task" + getId() + "/i/";
        }
        if (getIntroPageEnabled() && introSecondsRemaining() > 0) {
            return "/task" + getId() + "/p/";
        }
        if (getPrimerPageEnabled() && primerSecondsRemaining() > 0) {
            return "/task" + getId() + "/w/";
        }
        return null;

    }

    public Long getSecondsRemainingForCurrentUrl() {
        if (getIntroPageEnabled() && introSecondsRemaining() > 0) {
            return introSecondsRemaining();
        }
        if (getPrimerPageEnabled() && primerSecondsRemaining() > 0) {
            return primerSecondsRemaining();
        }
        if (interactionSecondsRemaining() > 0) {
            return interactionSecondsRemaining();
        }
        return -1l;

    }

    public Long introSecondsRemaining() {
        return this.getIntroEndTime() - DateUtils.now();
    }

    public Long getIntroEndTime() {
        return this.taskStartTimestamp + DateUtils.toMilliseconds(this.getIntroTime());
    }

    public Long primerSecondsRemaining() {
        return this.getPrimerEndTime() - DateUtils.now();
    }

    public Long getPrimerEndTime() {
        return getIntroEndTime() + DateUtils.toMilliseconds(getPrimerTime());
    }

    public Long interactionSecondsRemaining() {
        return this.getInteractionEndTime() - DateUtils.now();
    }

    public Long getInteractionEndTime() {
        return getPrimerEndTime() + DateUtils.toMilliseconds(getInteractionTime());
    }

    public ArrayList<SessionSchedule> getSessionSchedules(String roundUrl) {
        ArrayList<SessionSchedule> schedules = new ArrayList<>();

        if (getIntroPageEnabled()) {
            schedules.add(new SessionSchedule(getTaskStartTimestamp(),
                    getIntroEndTime(),this,null,null,
                    roundUrl+"/task/" + getId() + "/i"));
        }
        if (getPrimerPageEnabled()) {
            schedules.add(new SessionSchedule(getIntroEndTime(),
                    getPrimerEndTime(),this,null,null,
                    roundUrl+"/task/" + getId() + "/p"));

        }
        schedules.add(new SessionSchedule(getPrimerEndTime(),
                getTaskEndTimestamp(),this,null,null,
                roundUrl+"/task/" + getId() + "/w"));

        return schedules;
    }
}
