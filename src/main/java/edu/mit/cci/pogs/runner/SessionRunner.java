package edu.mit.cci.pogs.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.round.RoundDao;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.dao.session.TeamCreationMethod;
import edu.mit.cci.pogs.model.dao.session.TeamCreationTime;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskgroup.TaskGroupDao;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.dao.teamhassubject.TeamHasSubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TeamHasSubject;
import edu.mit.cci.pogs.runner.wrappers.RoundWrapper;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.runner.wrappers.TeamWrapper;
import edu.mit.cci.pogs.service.SessionService;
import edu.mit.cci.pogs.service.TaskGroupService;

@Component
public class SessionRunner implements Runnable {


    private static Map<Long, SessionRunner> liveRunners = new HashMap<>();

    private boolean sessionHasStarted = false;

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
            liveRunners.remove(sessionId);
        }
    }

    private SessionWrapper session;


    private List<Subject> subjectList;

    private Map<String, Subject> checkedInWaitingSubjectList;


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
    private CompletedTaskDao completedTaskDao;


    public void init() {

        configureSession();
        setupRounds(this.session);
        setupTaskList(this.session);
        setupSubjectList(this.session);

    }

    private void runSession() {
        while (!session.isSessionStatusDone()) {

            if ((allSubjectsAreWaiting() || sessionIsReadyToStart())
                    && !sessionHasStarted) {
                sessionHasStarted = true;
                startSession();
            }
        }
        SessionRunner.removeSessionRunner(session.getId());
    }


    private void configureRound(SessionWrapper session, RoundWrapper round) {
        // if task playmode == playlist
        if (session.isTaskExecutionModeSequential()) {
            if ((session.getTeamCreationMoment().equals(
                    TeamCreationTime.BEGINING_SESSION.getId().toString())) ||
                    session.getTeamCreationMoment().equals(TeamCreationTime.BEGINING_ROUND)) {
                createTeams(session, null, round);
                createCompletedTasks(session, round, true);
                setupStartingTimes(session, round, null);
                session.createSessionSchedule();

            } else {
                //TODO:Handle before task , team and completed task creation
            }

        } else {
            //TODO:Handle multi task and task team and completed task creation
        }

    }


    private void setupStartingTimes(SessionWrapper session, RoundWrapper round, RoundWrapper prevRound) {

        //if firstRound
        if(prevRound == null) {
            round.setRoundStartTimestamp(session.getSessionStartDate().getTime() + session.getIntroAndSetupTime());
        }else{
            round.setRoundStartTimestamp(round.getRoundFinishTimestamp());
        }

        Long elapsedTime = round.getRoundStartTimestamp();

        for (TaskWrapper tw : session.getTaskList()) {
            TaskWrapper newTw = new TaskWrapper(tw);
            newTw.setCompletedTasks(tw.getCompletedTasks());
            round.getTasks().add(newTw);
            newTw.setTaskStartTimestamp(elapsedTime);
            elapsedTime = newTw.getTotalTaskTime();
        }

    }

    private void createCompletedTasks(SessionWrapper session, RoundWrapper round, boolean hasOrder) {
        int taskOrderCounter = 1;
        for (Task task : session.getTaskList()) {
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

    private void createCompletedTask(Team team, Subject subject, Round currentRound, Task task, Integer order) {
        CompletedTask ct = new CompletedTask();
        ct.setCompledTaskOrder(order.shortValue());
        ct.setRoundId(currentRound.getId());
        ct.setTaskId(task.getId());

        if (subject != null) {
            ct.setSubjectId(subject.getId());
        }
        if (team != null) {
            ct.setTeamId(team.getId());
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
            //TODO: Handle the other kinds of team creation, Matrix, research defined
        }

        List<Team> teams = teamDao.listByRoundId(round.getId());
        List<TeamWrapper> teamWrappers = new ArrayList<>();
        for (Team t : teams) {

            TeamWrapper teamWrapper = new TeamWrapper(t);

            for (TeamHasSubject ths : teamHasSubjectDao.listByTeamId(t.getId())) {
                teamWrapper.getSubjects().add(checkedInWaitingSubjectList.get(ths.getSubjectId()));
            }
            teamWrappers.add(teamWrapper);
        }
        if (task != null) {
            task.setTaskTeams(teamWrappers);
        }
        round.setRoundTeams(teamWrappers);


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
    }

    private void setupRounds(SessionWrapper sessionz) {
        List<Round> rounds = roundDao.listBySessionId(sessionz.getId());

        if (rounds.size() == 0) {
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


    public SessionWrapper getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = new SessionWrapper(session);
    }
}
