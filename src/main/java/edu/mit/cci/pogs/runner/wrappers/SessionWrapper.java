package edu.mit.cci.pogs.runner.wrappers;

import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.dao.session.TaskExecutionType;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.service.SessionService;
import edu.mit.cci.pogs.utils.DateUtils;

public class SessionWrapper extends Session {


    public SessionWrapper(Session session) {
        super(session);
        this.sessionRounds = new ArrayList<>();
        this.taskList = new ArrayList<>();
    }


    private List<RoundWrapper> sessionRounds;

    private List<TaskWrapper> taskList;


    public RoundWrapper getCurrentRound() {
        for (RoundWrapper rw : sessionRounds) {
            if (rw.isHappeningNow(new Date().getTime()))
                return rw;
        }
        return null;
    }

    public RoundWrapper getNextRound() {
        for (int i = 0; i < sessionRounds.size(); i++) {
            RoundWrapper rw = sessionRounds.get(i);
            if (rw.isHappeningNow(new Date().getTime())) {
                if (i + 1 != sessionRounds.size()) {
                    return rw;
                }
                return null;
            }
        }
        return null;
    }

    public Long getSecondsRemainingForSession() {
        Integer roundSize = this.sessionRounds.size();
        if(roundSize!= 0 ){
            return sessionRounds.get(roundSize -1 ).getTotalRoundTime() * roundSize;
        }
        return 0l;
    }

    public Long getSecondsRemainingForCurrentUrl() {
        if (!this.isTaskExecutionModeSequential()) {
            return 0l;
        }
        //if session intro has already gone
        if (getTimeToStart() > 0) {
            return getTimeToStart();
        }
        //if session intro has already gone
        if (getIntroPageEnabled() && introSecondsRemaining() > 0) {
            return introSecondsRemaining();
        }
        //if session change name has already gone
        if (getDisplayNameChangePageEnabled() && getDisplayNameChangeEndTime() > 0) {
            return getDisplayNameChangeEndTime();
        }
        //if session roster has already gone
        if (getRosterPageEnabled() && getRosterPageEndTime() > 0) {
            return getRosterPageEndTime();
        }
        //check rounds
        RoundWrapper r = getCurrentRound();
        if (r != null) {
            TaskWrapper tw = r.getNextTask();
            if (tw != null) {
                return tw.getSecondsRemainingForCurrentUrl();
            } else {
                RoundWrapper rw = getNextRound();
                if (rw != null) {
                    TaskWrapper tw2 = rw.getNextTask();
                    return tw2.getSecondsRemainingForCurrentUrl();
                } else {
                    if (getDonePageEnabled()) {
                        return getSecondsRemainingForSession();
                    } else {
                        return getSecondsRemainingForSession();
                    }
                }
            }
        } else {
            return 0l;
        }
    }


    public Long getSecondsRemainingForCurrentRound() {
        if(this.getTimeToStart() >0) {
            return 0l;
        }
        if (!this.isTaskExecutionModeSequential()) {
            return 0l;
        }
        RoundWrapper rw = getCurrentRound();
        if(rw==null) {
            return 0l;
        }else{
            return rw.getTotalRoundTime() - DateUtils.now();
        }
    }

    public String getNextUrl() {
        if (!this.isTaskExecutionModeSequential()) {
            return "";
        }
        //if session intro has already gone
        if (getIntroPageEnabled() && introSecondsRemaining() > 0) {
            return "/intro";
        }
        //if session change name has already gone
        if (getDisplayNameChangePageEnabled() && getDisplayNameChangeEndTime() > 0) {
            return "/display_name";
        }
        //if session roster has already gone
        if (getRosterPageEnabled() && getRosterPageEndTime() > 0) {
            return "/roster";
        }
        //check rounds
        RoundWrapper r = getCurrentRound();
        if (r != null) {
            TaskWrapper tw = r.getNextTask();
            if (tw != null) {
                return r.getUrl() + tw.getNextUrl();
            } else {
                RoundWrapper rw = getNextRound();
                if (rw != null) {
                    TaskWrapper tw2 = rw.getNextTask();
                    return r.getUrl() +tw2.getNextUrl();
                } else {
                    if (getDonePageEnabled()) {
                        return "";
                    } else {
                        return getDoneRedirectUrl();
                    }
                }
            }
        } else {
            return getDoneRedirectUrl();
        }
    }


    public List<TaskWrapper> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskWrapper> taskList) {
        this.taskList = taskList;
    }

    public List<RoundWrapper> getSessionRounds() {
        return sessionRounds;
    }

    public void setSessionRounds(List<RoundWrapper> sessionRounds) {
        this.sessionRounds = sessionRounds;
    }


    public boolean isReadyToStart() {
        return this.isTooLate();
    }

    public boolean isReadyToInitiateStart() {
        return DateUtils.now()  + 2*1000 > this.getSessionStartDate().getTime() ;
    }


    private boolean isTooLate() {

        return DateUtils.now() > this.getSessionStartDate().getTime();
    }

    private long getTimeToStart() {
        return this.getSessionStartDate().getTime() - DateUtils.now();
    }



    //waiting room

    public long getTimeToWaitingRoomOpens() {
        return getTimeToStart() - SessionService.WAITING_ROOM_OPEN_INIT_WINDOW + 5;//possible conversion
    }

    public long introSecondsRemaining() {
        return this.getIntroEndTime() - DateUtils.now();
    }

    private long getIntroEndTime() {
        return this.getSessionStartDate().getTime() + DateUtils.toMilliseconds(this.getIntroTime());
    }

    public long displayNameChangeSecondsRemaining() {
        return getDisplayNameChangeEndTime() - DateUtils.now();
    }

    private long getDisplayNameChangeEndTime() {
        return getIntroEndTime() + DateUtils.toMilliseconds(this.getDisplayNameChangeTime());
    }

    private long rosterPageSecondsRemaining() {
        return getRosterPageEndTime() - DateUtils.now();
    }

    private long getRosterPageEndTime() {
        return getDisplayNameChangeEndTime() + DateUtils.toMilliseconds(this.getRosterTime());
    }

    public boolean isTaskExecutionModeSequential() {
        return this.getTaskExecutionType().equals(
                TaskExecutionType.SEQUENTIAL_FIXED_ORDER.getId().toString()) ||
                this.getTaskExecutionType().equals(
                        TaskExecutionType.SEQUENTIAL_RANDOM_ORDER.getId().toString());
    }


    public boolean isSessionStatusDone() {
        return getStatus()
                .equals(SessionStatus.DONE.getId().toString());
    }

    public Long getIntroAndSetupTime() {
        Long total = 0l;
        if (this.getIntroPageEnabled()) {
            total += DateUtils.toMilliseconds(this.getIntroTime());
        }
        if (this.getDisplayNameChangePageEnabled()) {
            total += DateUtils.toMilliseconds(this.getDisplayNameChangeTime());
        }
        if (this.getRosterPageEnabled()) {
            total += DateUtils.toMilliseconds(this.getRosterTime());
        }
        return total;
    }

}
