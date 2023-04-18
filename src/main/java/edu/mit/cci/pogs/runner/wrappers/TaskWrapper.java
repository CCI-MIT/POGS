
package edu.mit.cci.pogs.runner.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.utils.DateUtils;

public class TaskWrapper extends Task {

    public static final int SCORING_BUFFER_TIME = 60;
    public static final int SCORING_PAGE_TIME = 60;
    private Long taskStartTimestamp;

    private List<CompletedTask> completedTasks;

    private List<TeamWrapper> taskTeams;
    public TaskWrapper() {

    }

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
        if (this.getScorePageEnabled()) {
            total += DateUtils.toMilliseconds(SCORING_BUFFER_TIME);
            total += DateUtils.toMilliseconds(SCORING_PAGE_TIME);
        }
        return total;
    }

    public Long getTaskWorkEndTime(){
        Long total =  getTaskStartTimestamp() + getTotalTaskTime();
        if (this.getScorePageEnabled()) {
            total =  total - DateUtils.toMilliseconds(SCORING_BUFFER_TIME);
            total = total - DateUtils.toMilliseconds(SCORING_PAGE_TIME);
        }
        return total;
    }
    public Long getTaskEndTimestampWithoutScoringPage() {
        return getTaskWorkEndTime();
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
        if (getScorePageEnabled()) {
            return "/task" + getId() + "/s/";
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
    public Long getTaskScoreEndTime(){

        return getTaskWorkEndTime() + DateUtils.toMilliseconds(SCORING_BUFFER_TIME) + DateUtils.toMilliseconds(SCORING_PAGE_TIME);
    }

    public ArrayList<SessionSchedule> getSessionSchedules(String roundUrl) {
        ArrayList<SessionSchedule> schedules = new ArrayList<>();

        if (getIntroPageEnabled()) {
            schedules.add(new SessionSchedule(getTaskStartTimestamp(),
                    getIntroEndTime(),this,null,null,
                    roundUrl+getTaskIntroUrl()));
        }
        if (getPrimerPageEnabled()) {
            schedules.add(new SessionSchedule(getIntroEndTime(),
                    getPrimerEndTime(),this,null,null,
                    roundUrl+getTaskPrimerUrl()));

        }

        schedules.add(new SessionSchedule(getPrimerEndTime(),
                getTaskWorkEndTime(),this,null,null,
                roundUrl+getTaskWorkUrl()));
        if(getScorePageEnabled()){
            schedules.add(new SessionSchedule(getTaskWorkEndTime(),
                    getTaskScoreEndTime(),this,null,null,
                    roundUrl+getTaskScoreUrl()));
        }

        return schedules;
    }
    public String getTaskIntroUrl(){
        return "/task/" + getId() + "/i";
    }
    public String getTaskPrimerUrl(){
        return "/task/" + getId() + "/p";
    }
    public String getTaskWorkUrl(){
        return "/task/" + getId() + "/w";
    }
    public String getTaskScoreUrl(){
        return "/task/" + getId() + "/s";
    }
}
