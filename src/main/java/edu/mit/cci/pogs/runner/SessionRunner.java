package edu.mit.cci.pogs.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.mit.cci.pogs.model.dao.chatentry.ChatEntryDao;
import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.round.RoundDao;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.dao.session.TaskExecutionType;
import edu.mit.cci.pogs.model.dao.session.TeamCreationMethod;
import edu.mit.cci.pogs.model.dao.session.TeamCreationTime;
import edu.mit.cci.pogs.model.dao.subjectattribute.SubjectAttributeDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.dao.teamhassubject.TeamHasSubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TeamHasSubject;
import edu.mit.cci.pogs.runner.wrappers.RoundWrapper;
import edu.mit.cci.pogs.runner.wrappers.SessionSchedule;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.runner.wrappers.TeamWrapper;
import edu.mit.cci.pogs.service.SessionService;
import edu.mit.cci.pogs.service.TaskGroupService;
import edu.mit.cci.pogs.utils.ColorUtils;
import edu.mit.cci.pogs.utils.DateUtils;

@Component
public class SessionRunner implements Runnable {


    private static Map<Long, SessionRunner> liveRunners = new HashMap<>();

    private boolean sessionHasStarted = false;

    private static final Logger _log = LoggerFactory.getLogger(SessionRunner.class);

    public static Collection<SessionRunner> getLiveRunners() {
        return liveRunners.values();
    }

    public static SessionRunner getSessionRunner(Long sessionId) {
        return liveRunners.get(sessionId);
    }

    public static void addSessionRunner(Long sessionId, SessionRunner sessionRunner) {
        if (liveRunners.get(sessionId) == null) {
            liveRunners.put(sessionId, sessionRunner);
        }
    }

    public static void removeSessionRunner(Long sessionId) {
        if (liveRunners.get(sessionId) != null) {
            liveRunners.get(sessionId).setShouldRun(false);
            liveRunners.remove(sessionId);
        }
    }

    private SessionWrapper session;


    private List<Subject> subjectList;

    private Map<String, Subject> checkedInWaitingSubjectList;

    private Map<Long, Subject> checkedInWaitingSubjectListById;

    @Autowired
    private SessionDao sessionDao;
    @Autowired
    private SessionService sessionService;

    @Autowired
    private TaskGroupService taskGroupService;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private RoundDao roundDao;

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private TeamHasSubjectDao teamHasSubjectDao;

    @Autowired
    private SubjectAttributeDao subjectAttributeDao;

    @Autowired
    private ChatEntryDao chatEntryDao;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    @Autowired
    private ApplicationContext context;

    private boolean shouldRun;

    private List<Thread> chatRunners = new ArrayList<>();

    public void init() {
        _log.info("Configuring session: " + session.getSessionSuffix());
        shouldRun = true;
        sessionHasStarted = false;
        configureSession();
        setupRounds(this.session);
        setupTaskList(this.session);
        setupSubjectList(this.session);

    }

    private void runSession() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        while (shouldRun) {

            if ((allSubjectsAreWaiting() || sessionIsReadyToStart())
                    && !sessionHasStarted) {
                _log.info("Starting session: " + session.getSessionSuffix());
                sessionHasStarted = true;
                startSession();
            }
            if (session.getSecondsRemainingForSession() < 0) {
                shouldRun = false;
            }
            if (session.getSessionSchedule() != null) {
                for (int i = 0; i < session.getSessionSchedule().size(); i++) {
                    SessionSchedule ss = session.getSessionSchedule().get(i);
                    _log.info("Starting session schedule (" + ss.getUrl() + "): " + simpleDateFormat.format(new Date(ss.getStartTimestamp())));
                    if (ss.getTaskReference() != null) {
                        if ((session.getTeamCreationMoment().equals(
                                TeamCreationTime.BEGINING_TASK.getId().toString()))) {
                            createTeams(session, null, session.getCurrentRound());
                            createCompletedTasks(session, session.getCurrentRound(), true);
                            //TODO handle team creation before task.
                        }
                    }
                    while (ss.isHappeningNow(DateUtils.now())) {

                    }
                    _log.info("Finishing session schedule: " + simpleDateFormat.format(new Date(ss.getEndTimestamp())));


                }
            }

        }
        SessionRunner.removeSessionRunner(session.getId());
        for (Thread th : this.chatRunners) {
            th.interrupt();
        }
        _log.info("Exiting session runner for session" + session.getSessionSuffix());
    }


    private void configureRound(SessionWrapper session, RoundWrapper round) {
        // if task playmode == playlist
        if (session.isTaskExecutionModeSequential()) {

            setupStartingTimes(session, round, null);

            if ((session.getTeamCreationMoment().equals(
                    TeamCreationTime.BEGINING_SESSION.getId().toString()))) {

                createTeams(session, null, round);

                createCompletedTasks(session, round, true);

            } else {
                //TODO:Handle before task , team and completed task creation
            }

            if (session.getTaskExecutionType().equals(TaskExecutionType.SEQUENTIAL_RANDOM_ORDER.getId().toString())) {
                session.randomizeTaskOrder();
            }

            session.createSessionSchedule();
            checkAndScheduleChatScripts(session);
            scheduleTaskScoring(session);

        } else {
            if ((session.getTeamCreationMoment().equals(
                    TeamCreationTime.BEGINING_SESSION.getId().toString()))) {

                setupStartingTimesMultiTask(session, round, null);

                createTeams(session, null, round);
                createCompletedTasks(session, round, true);
                session.createSessionSchedule();
                checkAndScheduleChatScripts(session);
                scheduleTaskScoring(session);

            }
        }

    }

    private void scheduleTaskScoring(SessionWrapper session) {
        for (TaskWrapper task : session.getTaskList()) {
            if (task.getShouldScore()) {//task.shouldBeScored
                ScoringRunner csr = (ScoringRunner) context.getBean("scoringRunner");
                _log.debug("Added task scoring: " + task.getId() + " - " + csr);
                csr.setSession(session);
                csr.setTaskWrapper(task);

                Thread thread = new Thread(csr);
                thread.start();
                //chatRunners.add(thread);
            }
        }
    }

    private void checkAndScheduleChatScripts(SessionWrapper session) {
        for (TaskWrapper task : session.getTaskList()) {
            if (task.getChatScriptId() != null) {
                //create new script scheduler
                ChatScriptRunner csr = (ChatScriptRunner) context.getBean("chatScriptRunner");
                csr.setSession(session);
                csr.setTaskWrapper(task);
                csr.setChatEntryList(chatEntryDao.listChatEntryByChatScript(task.getChatScriptId()));
                Thread thread = new Thread(csr);
                thread.start();
                chatRunners.add(thread);
            }
        }
    }


    private void setupStartingTimesMultiTask(SessionWrapper session, RoundWrapper round, RoundWrapper prevRound) {


        Long now = new Date().getTime() + 1000 * 20;
        session.setSessionStartDate(new Timestamp(now));

        Long taskStartTime = session.getSessionStartDate().getTime() + session.getIntroAndSetupTime();
        round.setRoundStartTimestamp(taskStartTime);

        for (TaskWrapper tw : session.getTaskList()) {

            tw.setCompletedTasks(tw.getCompletedTasks());
            tw.setTaskStartTimestamp(taskStartTime);
            tw.setInteractionTime(session.getFixedInteractionTime());
            tw.setIntroPageEnabled(false);
            tw.setIntroTime(0);
            tw.setPrimerPageEnabled(false);
            tw.setPrimerTime(0);

            round.getTasks().add(tw);
        }

    }

    private void setupStartingTimes(SessionWrapper session, RoundWrapper round, RoundWrapper prevRound) {

        //if firstRound
        if (prevRound == null) {
            Long now = new Date().getTime() + 1000 * 20;
            session.setSessionStartDate(new Timestamp(now));
            round.setRoundStartTimestamp(session.getSessionStartDate().getTime() + session.getIntroAndSetupTime());
        } else {
            round.setRoundStartTimestamp(round.getRoundFinishTimestamp());
        }

        Long elapsedTime = round.getRoundStartTimestamp();

        for (TaskWrapper tw : session.getTaskList()) {

            tw.setCompletedTasks(tw.getCompletedTasks());
            round.getTasks().add(tw);
            tw.setTaskStartTimestamp(elapsedTime);
            elapsedTime += tw.getTotalTaskTime();
        }

    }

    private void createCompletedTasks(SessionWrapper session, RoundWrapper round, boolean hasOrder) {
        int taskOrderCounter = 1;
        for (TaskWrapper task : session.getTaskList()) {
            if (task.getSoloTask()) {
                for (Subject subject : checkedInWaitingSubjectList.values()) {
                    createCompletedTask(null, subject, round, task, taskOrderCounter);
                }
            } else {
                for (TeamWrapper team : round.getRoundTeams()) {
                    createCompletedTask(team.getTeam(), null, round, task, taskOrderCounter);
                }
            }
            if (hasOrder) {
                taskOrderCounter++;
            }
        }

        List<CompletedTask> completedTasks = completedTaskDao.listByRoundId(round.getId());
        for (CompletedTask ct : completedTasks) {
            for (TaskWrapper taskWrapper : session.getTaskList()) {
                if (ct.getTaskId() == taskWrapper.getId()) {
                    taskWrapper.getCompletedTasks().add(ct);
                    continue;
                }
            }
        }
    }

    private void createCompletedTask(Team team, Subject subject, Round currentRound, TaskWrapper task, Integer order) {
        CompletedTask ct = new CompletedTask();
        ct.setCompledTaskOrder(order.shortValue());
        ct.setRoundId(currentRound.getId());
        ct.setTaskId(task.getId());

        ct.setStartTime(new Timestamp(task.getTaskStartTimestamp()));
        ct.setExpectedFinishTime(new Timestamp(task.getTaskEndTimestamp()));

        if (subject != null) {
            ct.setSubjectId(subject.getId());
            ct.setSolo("true");
        }
        if (team != null) {
            ct.setTeamId(team.getId());
            ct.setSolo("false");
        }
        completedTaskDao.create(ct);
    }


    private void createTeams(SessionWrapper session, TaskWrapper task, RoundWrapper round) {
        if (session.getTeamCreationMethod().equals(
                TeamCreationMethod.RANDOMLY_SPECIFIC_SIZE.getId().toString())) {
            List<List<Subject>> subjects = createRandomTeamsOfSize(this.checkedInWaitingSubjectList,
                    session.getTeamMinSize());
            for (List<Subject> teamConfig : subjects) {
                createTeam(task, teamConfig, session, round);
            }
        } else {
            if (session.getTeamCreationMethod().equals(
                    TeamCreationMethod.RANDOMLY_MATRIX.getId().toString())) {
                String matrix = session.getTeamCreationMatrix();
                String lines[] = matrix.split("\r\n");
                int checkedInSubjects = checkedInWaitingSubjectList.keySet().size();
                String[] matrixLineConfig = null;
                for (String line : lines) {
                    if (Integer.parseInt(line.substring(0, 1)) == checkedInSubjects) {
                        matrixLineConfig = line.split(",");
                    }
                }
                if (matrixLineConfig != null) {
                    List<List<Subject>> subjects = new ArrayList<>();
                    List<Subject> subjectsLeft;
                    List<Subject> checkedSubsNotInGroups = new ArrayList<>();
                    checkedSubsNotInGroups.addAll(this.checkedInWaitingSubjectList.values());
                    Collections.shuffle(checkedSubsNotInGroups);
                    for (int i = 1; i < matrixLineConfig.length; i++) {
                        Integer total = Integer.parseInt(matrixLineConfig[i]);
                        subjectsLeft = new ArrayList<>();
                        subjectsLeft.addAll(checkedSubsNotInGroups);

                        List<Subject> currentTeam = new ArrayList<>();
                        for (Subject subject : subjectsLeft) {
                            if (currentTeam.size() != total) {
                                checkedSubsNotInGroups.remove(subject);
                                currentTeam.add(subject);
                            }
                        }
                        subjects.add(currentTeam);
                    }
                    for (List<Subject> teamConfig : subjects) {
                        createTeam(task, teamConfig, session, round);
                    }
                }
                //did not find the right line setup (should not happen)
            }

        }

        List<Team> teams = teamDao.listByRoundId(round.getId());
        List<TeamWrapper> teamWrappers = new ArrayList<>();
        for (Team t : teams) {

            TeamWrapper teamWrapper = new TeamWrapper(t);

            for (TeamHasSubject ths : teamHasSubjectDao.listByTeamId(t.getId())) {
                teamWrapper.getSubjects().add(checkedInWaitingSubjectListById.get(ths.getSubjectId()));
            }
            teamWrappers.add(teamWrapper);
        }
        if (task != null) {
            task.setTaskTeams(teamWrappers);
        }
        round.setRoundTeams(teamWrappers);


        assignColorsToTeamMembers(round.getRoundTeams());
    }

    private void assignColorsToTeamMembers(List<TeamWrapper> roundTeams) {

        for (TeamWrapper tw : roundTeams) {
            List<Subject> subjectList = tw.getSubjects();
            Color[] colors = ColorUtils.generateVisuallyDistinctColors(
                    ((subjectList.size() > 10) ? (subjectList.size()) : (10)),
                    ColorUtils.MIN_COMPONENT, ColorUtils.MAX_COMPONENT);

            for (int i = 0; i < subjectList.size(); i++) {
                addColorSubjectAttribute(subjectList.get(i),
                        ColorUtils.SUBJECT_DEFAULT_BACKGROUND_COLOR_ATTRIBUTE_NAME, colors[i]);
                addColorSubjectAttribute(subjectList.get(i),
                        ColorUtils.SUBJECT_DEFAULT_FONT_COLOR_ATTRIBUTE_NAME,
                        ColorUtils.generateFontColorBasedOnBackgroundColor(colors[i]));
            }
        }
    }

    private void addColorSubjectAttribute(Subject su1, String attributeName, Color color) {
        List<SubjectAttribute> attributeList = subjectAttributeDao.listBySubjectId(su1.getId());
        if (attributeList != null && attributeList.size() > 0) {
            for (SubjectAttribute sa : attributeList) {
                if(sa.getAttributeName().equals(ColorUtils.SUBJECT_DEFAULT_BACKGROUND_COLOR_ATTRIBUTE_NAME)){
                    return;
                }
            }
        }
        Subject su = su1;
        SubjectAttribute sa = new SubjectAttribute();
        sa.setAttributeName(attributeName);

        sa.setStringValue(String.format("#%02x%02x%02x", color.getRed(),
                color.getGreen(), color.getBlue()));

        sa.setSubjectId(su.getId());
        subjectAttributeDao.create(sa);
    }


    private static List<List<Subject>> createRandomTeamsOfSize(Map<String, Subject> subjectList,
                                                               Integer teamMinSize) {
        Integer numberOfGroups = subjectList.values().size() / teamMinSize;
        Random random = new Random();
        List<Subject> checkedInSubjects = new ArrayList<>();
        checkedInSubjects.addAll(subjectList.values());
        List<List<Subject>> ret = new ArrayList<>();
        List<List<Subject>> fullGroups = new ArrayList<>();
        for (int i = 0; i < numberOfGroups; i++) {
            ret.add(new ArrayList<>());
        }

        while (checkedInSubjects.size() != 0 && ret.size() != 0) {
            Integer randomSubjIndex = random.nextInt(checkedInSubjects.size());
            Subject randomSubj = checkedInSubjects.get(randomSubjIndex);
            List<Subject> group = ret.get(random.nextInt(ret.size()));
            if (group.size() != teamMinSize) {
                group.add(randomSubj);
                checkedInSubjects.remove(randomSubj);
            }
            if (group.size() == teamMinSize) {
                fullGroups.add(group);
                ret.remove(group);
            }

        }

        return fullGroups;

    }

    private void createTeam(Task task, List<Subject> subjectsInTeam, SessionWrapper session,
                            Round round) {
        Team team = new Team();
        team.setRoundId(round.getId());
        team.setSessionId(session.getId());
        if (task != null) {
            team.setTaskId(task.getId());
        }
        team = teamDao.create(team);
        for (Subject subject : subjectsInTeam) {
            TeamHasSubject ths = new TeamHasSubject();
            ths.setSubjectId(subject.getId());
            ths.setTeamId(team.getId());
            teamHasSubjectDao.create(ths);
        }
    }

    private boolean allSubjectsAreWaiting() {
        return checkedInWaitingSubjectList.keySet().size() == subjectList.size();
    }

    private boolean sessionIsReadyToStart() {

        return session.isReadyToInitiateStart();
    }

    private void setupSubjectList(SessionWrapper session) {
        subjectList = sessionService.listSubjectsBySessionId(session.getId());
        checkedInWaitingSubjectList = new HashMap<>();
        checkedInWaitingSubjectListById = new HashMap<>();
    }

    private void setupRounds(SessionWrapper sessionz) {
        List<Round> rounds = roundDao.listBySessionId(sessionz.getId());

        if (rounds != null && rounds.size() == 0) {
            if (sessionz.getRoundsEnabled()) {
                for (int i = 0; i < sessionz.getNumberOfRounds(); i++) {
                    createRound(i + 1, sessionz.getId());
                }
            } else {
                createRound(1, sessionz.getId());
            }
        }
        rounds = roundDao.listBySessionId(session.getId());

        //TODO: extract up to here to rounds service

        for (Round r : rounds) {
            sessionz.getSessionRounds().add(new RoundWrapper(r));
        }

    }

    private void createRound(int i, Long sessionId) {
        Round round = new Round();
        round.setSessionId(sessionId);
        round.setRoundNumber(i);
        roundDao.create(round);

    }

    public void subjectCheckIn(Subject subject) {
        checkedInWaitingSubjectList.put(subject.getSubjectExternalId(), subject);
        checkedInWaitingSubjectListById.put(subject.getId(), subject);

    }

    private void setupTaskList(SessionWrapper sessionz) {
        List<Task> taskList = new ArrayList<>();
        List<SessionHasTaskGroup> taskGroupList = sessionService.listSessionHasTaskGroupBySessionId(sessionz.getId());
        for (SessionHasTaskGroup sshtg : taskGroupList) {
            List<TaskGroupHasTask> tghtList = taskGroupService.listTaskGroupHasTaskByTaskGroup(sshtg.getTaskGroupId());
            for (TaskGroupHasTask tght : tghtList) {
                Task task = taskDao.get(tght.getTaskId());
                taskList.add(new TaskWrapper(task));
            }
        }

        for (Task t : taskList) {
            sessionz.getTaskList().add(new TaskWrapper(t));
        }

        //TODO: Extract this method to task service
    }

    private void startSession() {
        session.setStatus(SessionStatus.STARTED.getId().toString());
        sessionDao.update(session);
        for (RoundWrapper rw : session.getSessionRounds()) {
            configureRound(session, rw);
        }
    }

    private void configureSession() {
        session.setStatus(SessionStatus.CONFIGURING.getId().toString());
        sessionDao.update(session);
    }

    @Override
    public void run() {
        init();
        runSession();
    }

    public Map<String, Subject> getAllCheckedInSubjects() {
        return checkedInWaitingSubjectList;
    }

    public SessionWrapper getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = new SessionWrapper(session);
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }
}
