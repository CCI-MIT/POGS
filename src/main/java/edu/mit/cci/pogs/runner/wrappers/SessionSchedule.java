package edu.mit.cci.pogs.runner.wrappers;

import java.util.Date;

public class SessionSchedule {

    private Long startTimestamp;

    private Long endTimestamp;

    private TaskWrapper taskReference;

    private RoundWrapper roundReference;

    private SessionWrapper sessionReference;

    private String url;

    public SessionSchedule(Long startTimestamp, Long finishTimestamp, TaskWrapper taskReference, RoundWrapper roundReference, SessionWrapper sessionReference, String url) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = finishTimestamp;
        this.taskReference = taskReference;
        this.roundReference = roundReference;
        this.sessionReference = sessionReference;
        this.url = url;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }



    public TaskWrapper getTaskReference() {
        return taskReference;
    }

    public void setTaskReference(TaskWrapper taskReference) {
        this.taskReference = taskReference;
    }

    public RoundWrapper getRoundReference() {
        return roundReference;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRoundReference(RoundWrapper roundReference) {
        this.roundReference = roundReference;
    }

    public SessionWrapper getSessionReference() {
        return sessionReference;
    }

    public void setSessionReference(SessionWrapper sessionReference) {
        this.sessionReference = sessionReference;
    }

    public boolean isAlreadyPassed() {
        Long currentTimestamp = new Date().getTime();
        return currentTimestamp >= getStartTimestamp() && currentTimestamp > getEndTimestamp();
    }

    public boolean isToBeDone() {
        Long currentTimestamp = new Date().getTime();
        return currentTimestamp < getStartTimestamp();
    }

    public boolean isHappeningNow(Long currentTimestamp) {
        return currentTimestamp >= getStartTimestamp() && currentTimestamp < getEndTimestamp();
    }
}
