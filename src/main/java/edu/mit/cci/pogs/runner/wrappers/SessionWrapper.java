package edu.mit.cci.pogs.runner.wrappers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.cci.pogs.model.dao.session.SessionScheduleType;
import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.dao.session.TaskExecutionType;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.service.SessionService;
import edu.mit.cci.pogs.utils.DateUtils;

public class SessionWrapper extends Session {


    private ArrayList<SessionSchedule> sessionSchedule;

    private static final Integer SCORING_TIME_PAGE = 1000*8;

    public SessionWrapper(Session session) {
        super(session);
        this.sessionRounds = new ArrayList<>();
        this.taskList = new ArrayList<>();
        this.feedbackCounter = new HashMap<>();
    }


    private List<RoundWrapper> sessionRounds;

    private List<TaskWrapper> taskList;

    private Map<Long, Map<String, Integer>> feedbackCounter;

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

    public Map<Long, Map<String, Integer>> getFeedbackCounter() {
        return feedbackCounter;
    }

    public void addSubjectContribution(Long completedTaskId, String subjectExternalId, Integer contribution) {
        Map<String, Integer> map = this.feedbackCounter.get(completedTaskId);
        if (map == null) {
            map = new HashMap<>();
        }
        Integer currentCount = map.get(subjectExternalId);
        if (currentCount == null) {
            currentCount = 0;
        }
        currentCount = currentCount + contribution;
        map.put(subjectExternalId, currentCount);
        this.feedbackCounter.put(completedTaskId, map);

    }

    public Long getSecondsRemainingForSession() {

        if (isSessionPerpetual()) {
            return 20 * 1000l;
        }
        Integer roundSize = this.sessionRounds.size();

        if (roundSize != 0) {
            long startRoundTime = this.getSessionStartDate().getTime();
            return (startRoundTime + sessionRounds.get(roundSize - 1).getTotalRoundTime() * roundSize) - DateUtils.now();
        }
        return -1l;
    }

    public Long getSecondsRemainingForCurrentUrl() {
        //if (!this.isTaskExecutionModeSequential()) {
        //    return 0l;
        // }

        if (isSessionPerpetual()) {

            return (new Date().getTime() + 1000 * 60 * 5);
        }
        if (this.sessionSchedule == null) {
            return getTimeToStart();
        }
        Integer sessionScheduleIndex = getSessionScheduleIndex();
        System.out.println("SESSION INDEX ----- " +sessionScheduleIndex  + " - " +(
                this.sessionSchedule.get(sessionScheduleIndex).getEndTimestamp() - DateUtils.now()));

        return this.sessionSchedule.get(sessionScheduleIndex).getEndTimestamp() - DateUtils.now();

    }

    public Boolean isSessionPerpetual(){
        if(getParentSessionId()!=null) {
            return false;
        }
        return this.getSessionScheduleType().equals(SessionScheduleType.PERPETUAL.getId().toString())||
                this.getSessionScheduleType().equals(SessionScheduleType.PERPETUAL_LANDING_PAGE.getId().toString());
    }

    public Long getSecondsRemainingForCurrentRound() {
        if (isSessionPerpetual()) {

            return (new Date().getTime() + 1000 * 60 * 5);
        }
        if (this.getTimeToStart() > 0) {
            return 0l;
        }
        RoundWrapper rw = getCurrentRound();
        if (rw == null) {
            return 0l;
        } else {
            return (rw.getRoundStartTimestamp() + rw.getTotalRoundTime()) - DateUtils.now();
        }
    }

    public String getCurrentUrl() {
        Integer sessionScheduleIndex = getSessionScheduleIndex();

        if (isSessionPerpetual()) {
            return "/waiting_room";
        }

        if (this.sessionSchedule == null || this.sessionSchedule.size() == 0) {
            return "/waiting_room";
        }
        return this.sessionSchedule.get(sessionScheduleIndex).getUrl();

    }

    public String getNextUrl() {

        if (isSessionPerpetual()) {
            return "";
        }

        if (this.sessionSchedule == null || this.sessionSchedule.size() == 0) {
            return "/intro";
        }
        Integer sessionScheduleIndex = getSessionScheduleIndex();


        if (sessionScheduleIndex + 1 < this.sessionSchedule.size()) {
            return this.sessionSchedule.get(sessionScheduleIndex + 1).getUrl();
        }
        //before getting the done, get the attributes ?
        return getDoneRedirectUrl();

    }


    public List<TaskWrapper> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskWrapper> taskList) {
        this.taskList = taskList;
    }

    public void randomizeTaskOrder() {
        Collections.shuffle(this.taskList);
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
        return DateUtils.now() + 10 * 1000 > this.getSessionStartDate().getTime();
    }


    public boolean isTooLate() {
        if(isSessionPerpetual()){
            return false;
        }
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

    private long getDonePageEndTime(Long lastRoundEndTime) {
        return lastRoundEndTime + SCORING_TIME_PAGE + DateUtils.toMilliseconds(this.getDonePageTime());
    }

    public boolean isTaskExecutionModeSequential() {
        return this.getTaskExecutionType().equals(
                TaskExecutionType.SEQUENTIAL_FIXED_ORDER.getId().toString()) ||
                this.getTaskExecutionType().equals(
                        TaskExecutionType.SEQUENTIAL_RANDOM_ORDER.getId().toString());
    }

    public boolean isTaskExecutionModeParallel() {
        return this.getTaskExecutionType().equals(
                TaskExecutionType.PARALLEL_FIXED_ORDER.getId().toString());
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


    public void createSessionSchedule() {

        this.sessionSchedule = new ArrayList<>();

        if (this.getTimeToStart() > 0) {
            this.sessionSchedule.add(new SessionSchedule(new Date().getTime()
                    , getSessionStartDate().getTime(), null,
                    null, this, "/waiting_room"));
        }
        if (getIntroPageEnabled()) {
            this.sessionSchedule.add(new SessionSchedule(
                    getSessionStartDate().getTime(), getIntroEndTime(), null,
                    null, this, "/intro"));
        }
        if (getDisplayNameChangePageEnabled()) {
            this.sessionSchedule.add(new SessionSchedule(
                    getIntroEndTime(), getDisplayNameChangeEndTime(), null,
                    null, this, "/display_name"));
        }
        if (getRosterPageEnabled()) {
            this.sessionSchedule.add(new SessionSchedule(
                    getDisplayNameChangeEndTime(), getRosterPageEndTime(), null,
                    null, this, "/roster"));
        }
        Long lastRoundEndTimestamp = -1l;
        for (RoundWrapper rw : sessionRounds) {
            ArrayList<SessionSchedule> rounds = rw.getSessionSchedules();
            rounds.stream().forEach(t -> t.setSessionReference(this));
            this.sessionSchedule.addAll(rounds);
            lastRoundEndTimestamp = rw.getRoundFinishTimestamp();
        }


        this.sessionSchedule.add(new SessionSchedule(lastRoundEndTimestamp
                , lastRoundEndTimestamp + SCORING_TIME_PAGE, null,
                null, this, "/scoring"));


        if (this.getDonePageEnabled()) {
            this.sessionSchedule.add(new SessionSchedule(lastRoundEndTimestamp
                    , getDonePageEndTime(lastRoundEndTimestamp), null,
                    null, this, "/done"));
        }

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if (this.isTaskExecutionModeParallel()) {
            removeIntrosAndPrimersFromSchedule();
        }

        System.out.println("Time of schedule creation : " + new Date());

        for (SessionSchedule ss : this.sessionSchedule) {

            System.out.println(simpleDateFormat.format(new Date(ss.getStartTimestamp())) + " - " + simpleDateFormat.format(new Date(ss.getEndTimestamp())) + " - " + ss.getUrl());
        }
    }

    private void removeIntrosAndPrimersFromSchedule() {
        List<SessionSchedule> allSchedules = new ArrayList<>(this.sessionSchedule);
        boolean foundOneTask = false;
        long taskFinalTimestamp = 0l;
        for (SessionSchedule ss : allSchedules) {
            if (ss.getTaskReference() != null) {
                if (!ss.getUrl().endsWith("/w")) {
                    this.sessionSchedule.remove(ss);
                } else {
                    if (!foundOneTask) {
                        foundOneTask = true;
                        taskFinalTimestamp = ss.getEndTimestamp();
                    } else {
                        this.sessionSchedule.remove(ss);
                    }
                }
            } else {
                if (ss.getUrl().endsWith("/done")) {
                    long doneTime = ss.getEndTimestamp() - ss.getStartTimestamp();
                    ss.setStartTimestamp(taskFinalTimestamp);
                    ss.setEndTimestamp(taskFinalTimestamp + doneTime);
                }
            }
        }
    }

    public ArrayList<SessionSchedule> getSessionSchedule() {
        return sessionSchedule;
    }

    public void setSessionSchedule(ArrayList<SessionSchedule> sessionSchedule) {
        this.sessionSchedule = sessionSchedule;
    }

    public boolean isCurrentScheduleTask() {
        Long now = DateUtils.now();
        for (int i = 0; i < this.sessionSchedule.size(); i++) {
            SessionSchedule ss = this.sessionSchedule.get(i);
            if (ss.isHappeningNow(now)) {
                return ss.getTaskReference() != null;
            }
        }
        return false;
    }

    public Integer getSessionScheduleIndex() {

        if (this.sessionSchedule == null) return 0;
        for (int i = 0; i < this.sessionSchedule.size(); i++) {
            SessionSchedule ss = this.sessionSchedule.get(i);
            if (ss.isHappeningNow(DateUtils.now())) {
                return i;
            }
        }
        return this.sessionSchedule.size() - 1;
    }
}
