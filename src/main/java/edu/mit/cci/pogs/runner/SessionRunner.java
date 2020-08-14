package edu.mit.cci.pogs.runner;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.mit.cci.pogs.model.dao.chatentry.ChatEntryDao;
import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.dao.round.RoundDao;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.session.SessionScheduleConditionToStartType;
import edu.mit.cci.pogs.model.dao.session.SessionScheduleType;
import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.dao.session.TaskExecutionType;
import edu.mit.cci.pogs.model.dao.session.TeamCreationMethod;
import edu.mit.cci.pogs.model.dao.session.TeamCreationTime;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.subjectattribute.SubjectAttributeDao;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.dao.teamhassubject.TeamHasSubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasSessionCheckIn;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TeamHasSubject;
import edu.mit.cci.pogs.runner.wrappers.RoundWrapper;
import edu.mit.cci.pogs.runner.wrappers.SessionSchedule;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;
import edu.mit.cci.pogs.runner.wrappers.TeamWrapper;
import edu.mit.cci.pogs.service.SessionService;
import edu.mit.cci.pogs.service.SubjectCommunicationService;
import edu.mit.cci.pogs.service.SubjectHasSessionCheckInService;
import edu.mit.cci.pogs.service.TaskGroupService;
import edu.mit.cci.pogs.service.TaskService;
import edu.mit.cci.pogs.service.TeamService;
import edu.mit.cci.pogs.utils.ColorUtils;
import edu.mit.cci.pogs.utils.DateUtils;
import edu.mit.cci.pogs.utils.StringUtils;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionRunner implements Runnable {

    private boolean sessionHasStarted = false;

    private static final Logger _log = LoggerFactory.getLogger(SessionRunner.class);

    private SessionWrapper session;


    private List<Subject> subjectList;

    private Map<String, Subject> checkedInWaitingSubjectList = new HashMap<>();

    private Map<Long, Subject> checkedInWaitingSubjectListById = new HashMap<>();

    private List<SubjectHasSessionCheckIn> subjectCheckInList;

    private Map<Long, List<Subject>> sessionsRelatedToPerpetual = new HashMap<>();

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
    private SubjectDao subjectDao;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    @Autowired
    private SubjectCommunicationService subjectCommunicationService;

    @Autowired
    private ExecutableScriptDao executableScriptDao;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SubjectHasSessionCheckInService subjectHasSessionCheckInService;

    @Autowired
    private ApplicationContext context;

    private boolean shouldRun;

    private volatile boolean threadShouldStop;

    private List<Thread> chatAndScriptRunners = new ArrayList<>();

    private JSONArray perpetualSubjectsMigratedToSpawnedSessions = new JSONArray();

    public void init() {
        _log.info("Configuring session: " + session.getSessionSuffix());
        shouldRun = true;
        threadShouldStop = false;
        sessionHasStarted = false;
        configureSession();
        setupRounds(this.session);
        setupTaskList(this.session);
        setupSubjectList(this.session);

    }


    private void runSession() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        while (shouldRun && !threadShouldStop) {

            if ((allSubjectsAreWaiting() || sessionIsReadyToStart())
                    && !sessionHasStarted) {
                _log.info("Starting session: " + session.getFullSessionName());
                sessionHasStarted = true;
                startSession();
                startSessionBeforeScriptIfAny();
            }
            if (session.getSessionSchedule() != null) {
                for (int i = 0; i < session.getSessionSchedule().size(); i++) {

                    SessionSchedule ss = session.getSessionSchedule().get(i);
                    long nowTS = DateUtils.now();
                    if(!ss.isAlreadyPassed(nowTS) && ss.isHappeningNow(nowTS)) {
                        _log.info("Starting session schedule (" + ss.getUrl() + "): " + simpleDateFormat.format(new Date(ss.getStartTimestamp())));



                            try{
                                Thread.sleep(ss.getEndTimestamp()-nowTS + 500);
                            }catch (InterruptedException inte){
                                _log.info("Session schedule interrupted: (" + ss.getUrl() + "):" + simpleDateFormat.format(DateUtils.now()));
                            }


                        _log.info("Finishing session schedule: (" + ss.getUrl() + "):" + simpleDateFormat.format(new Date(ss.getEndTimestamp())));
                    }
                }
            }
            if (session.getSecondsRemainingForSession() < 0) {
                shouldRun = false;
                startSessionAfterScriptIfAny();
            }

        }
        SessionRunnerManager.removeSessionRunner(session.getId());
        for (Thread th : this.chatAndScriptRunners) {
            if(th!=null && th.isAlive()) {
                th.interrupt();
            }
        }
        endSession();
        _log.info("Exiting session runner for session: " + session.getSessionSuffix());
    }

    private void startSessionAfterScriptIfAny() {
        if(session.getAfterSessionScriptId()!=null){
            SessionRelatedScriptRunner csr = (SessionRelatedScriptRunner) context.getBean("sessionRelatedScriptRunner");
            _log.debug("Added before session script : " + csr);
            csr.setSession(session);
            csr.setExecutableScriptId(session.getAfterSessionScriptId());


            Thread thread = new Thread(csr);
            thread.start();
            chatAndScriptRunners.add(thread);
        }
    }

    private void startSessionBeforeScriptIfAny() {
        if(session.getBeforeSessionScriptId()!=null){
            SessionRelatedScriptRunner csr = (SessionRelatedScriptRunner) context.getBean("sessionRelatedScriptRunner");
            _log.debug("Added before session script : " + csr);
            csr.setSession(session);
            csr.setExecutableScriptId(session.getBeforeSessionScriptId());
            Thread thread = new Thread(csr);
            thread.start();
            chatAndScriptRunners.add(thread);
        }
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



            session.createSessionSchedule();
            checkAndScheduleChatScripts(session);
            scheduleTaskRelatedThreads(session);
            scheduleTaskReplays(session);

        } else {
            if ((session.getTeamCreationMoment().equals(
                    TeamCreationTime.BEGINING_SESSION.getId().toString()))) {

                setupStartingTimesMultiTask(session, round, null);

                createTeams(session, null, round);
                createCompletedTasks(session, round, true);
                session.createSessionSchedule();
                checkAndScheduleChatScripts(session);
                scheduleTaskRelatedThreads(session);

            }
        }

    }

    private void scheduleTaskReplays(SessionWrapper session) {
        // RUN

        if(session.getRobotSessionEventScriptId()!=null || session.getRobotSessionEventSourceId()!=null){
            SessionReplayScriptProcessor srsp = (SessionReplayScriptProcessor) context.getBean("sessionReplayScriptProcessor");

            srsp.setSessionScriptToReplayFrom(session.getRobotSessionEventScriptId());
            srsp.setSessionToReplayFrom(session.getRobotSessionEventSourceId());
            JSONArray sessionEvents = srsp.processAndGenerateScriptEntries();
            for (TaskWrapper task : session.getTaskList()) {
                TaskEventReplayRunner csr = (TaskEventReplayRunner) context.getBean("taskEventReplayRunner");
                csr.setSession(session);
                csr.setTaskWrapper(task);
                csr.setSessionEvents(sessionEvents);
                Thread thread = new Thread(csr);
                thread.start();
                chatAndScriptRunners.add(thread);

            }
        }
    }

    private void scheduleTaskRelatedThreads(SessionWrapper session) {
        scheduleTaskBeforeWork(session);
        scheduleTaskAfterWork(session);
        scheduleTaskScoring(session);
    }


    private void scheduleTaskScoring(SessionWrapper session) {
        for (TaskWrapper task : session.getTaskList()) {
            if (task.getShouldScore()) {
                for (CompletedTask ct : task.getCompletedTasks()) {
                    TaskPlugin pl = TaskPlugin.getTaskPlugin(task.getTaskPluginType());
                    TaskConfiguration taskConfiguration = taskService.getTaskConfiguration(task.getId());
                    if (taskConfiguration.getScoreScriptId() != null || pl != null) {
                        ScoringRunner csr = (ScoringRunner) context.getBean("scoringRunner");
                        _log.debug("Added task scoring: " + task.getId() + " - " + csr);
                        csr.setSession(session);
                        csr.setTaskWrapper(task);
                        csr.setTaskPlugin(pl);
                        csr.setCompletedTask(ct);

                        Thread thread = new Thread(csr);
                        thread.start();
                        chatAndScriptRunners.add(thread);
                    }
                }
            }
        }
    }
    private void scheduleTaskBeforeWork(SessionWrapper session) {
        for (TaskWrapper task : session.getTaskList()) {
            TaskPlugin pl = TaskPlugin.getTaskPlugin(task.getTaskPluginType());
            TaskConfiguration taskConfiguration = taskService.getTaskConfiguration(task.getId());
            if (pl != null) {
                if (taskConfiguration.getBeforeWorkScriptId()!= null ||pl.getTaskBeforeWorkJsContent() != null) {
                    TaskBeforeWorkRunner csr = (TaskBeforeWorkRunner) context.getBean("taskBeforeWorkRunner");
                    _log.debug("Added task before work: " + task.getId() + " - " + csr + " - # of completed tasks: " + task.getCompletedTasks().size() );
                    csr.setSession(session);
                    csr.setTaskWrapper(task);
                    csr.setTaskPlugin(pl);

                    Thread thread = new Thread(csr);
                    thread.start();
                    chatAndScriptRunners.add(thread);
                }

            }
        }
    }
    private void scheduleTaskAfterWork(SessionWrapper session) {
        for (TaskWrapper task : session.getTaskList()) {
            TaskPlugin pl = TaskPlugin.getTaskPlugin(task.getTaskPluginType());
            TaskConfiguration taskConfiguration = taskService.getTaskConfiguration(task.getId());
            if (pl != null) {
                if (taskConfiguration.getAfterWorkScriptId()!= null || (pl.getTaskAfterWorkJsContent() != null)) {
                    TaskAfterWorkRunner csr = (TaskAfterWorkRunner) context.getBean("taskAfterWorkRunner");
                    _log.debug("Added task after work: " + task.getId() + " - " + csr+ " - # of completed tasks: " + task.getCompletedTasks().size() );
                    csr.setSession(session);
                    csr.setTaskWrapper(task);
                    csr.setTaskPlugin(pl);

                    Thread thread = new Thread(csr);
                    thread.start();
                    chatAndScriptRunners.add(thread);
                }

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
                chatAndScriptRunners.add(thread);
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
            elapsedTime += (tw.getTotalTaskTime());// 1 second shift so that tasks slightly overlap
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
                if (ct.getTaskId().longValue() == taskWrapper.getId().longValue()) {
                    taskWrapper.getCompletedTasks().add(ct);
                    //continue;
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
                Subject s = checkedInWaitingSubjectListById.get(ths.getSubjectId());
                if(s == null){
                    s = subjectDao.get(s.getId());
                }
                teamWrapper.getSubjects().add(s);
            }
            teamWrappers.add(teamWrapper);
        }
        if (task != null) {
            task.setTaskTeams(teamWrappers);
        }
        round.setRoundTeams(teamWrappers);


        assignColorsToTeamMembers(round.getRoundTeams());

        if(session.getDisplayNameGenerationEnabled() && session.getDisplayNameGenerationType()!=null){
            assignDisplayNamesToTeamMembers(round.getRoundTeams(),session.getDisplayNameGenerationType().charAt(0));
        }
    }
    private void assignDisplayNamesToTeamMembers(List<TeamWrapper> roundTeams, char generationType) {
        for (TeamWrapper tw : roundTeams) {
            List<Subject> subjectList = tw.getSubjects();
            String[] displayNames = StringUtils.getUniqueNamesOfSize(subjectList.size(),generationType);
            for(int i = 0; i <subjectList.size(); i++){
                Subject su = subjectList.get(i);
                if(displayNames.length>i) {
                    su.setSubjectDisplayName(displayNames[i]);
                    subjectDao.update(su);
                }
            }
        }

    }
    private void assignColorsToTeamMembers(List<TeamWrapper> roundTeams) {

        for (TeamWrapper tw : roundTeams) {
            List<Subject> subjectList = tw.getSubjects();
            Color[] colors = ColorUtils.generateVisuallyDistinctColors(
                    ((subjectList.size() > 10) ? (subjectList.size()) : (10)),
                    ColorUtils.MIN_COMPONENT, ColorUtils.MAX_COMPONENT);

            for (int i = 0; i < subjectList.size(); i++) {

                List<SubjectAttribute> attributeList = subjectAttributeDao.listBySubjectId(
                        subjectList.get(i).getId());
                if (attributeList != null && attributeList.size() > 0) {
                    for (SubjectAttribute sa : attributeList) {
                        if (sa.getAttributeName().equals(ColorUtils.SUBJECT_DEFAULT_BACKGROUND_COLOR_ATTRIBUTE_NAME)) {
                            return;
                        }
                    }
                }

                addColorSubjectAttribute(subjectList.get(i),
                        ColorUtils.SUBJECT_DEFAULT_BACKGROUND_COLOR_ATTRIBUTE_NAME, colors[i]);
                addColorSubjectAttribute(subjectList.get(i),
                        ColorUtils.SUBJECT_DEFAULT_FONT_COLOR_ATTRIBUTE_NAME,
                        ColorUtils.generateFontColorBasedOnBackgroundColor(colors[i]));
            }
        }
    }

    private void addColorSubjectAttribute(Subject su1, String attributeName, Color color) {

        Subject su = su1;
        SubjectAttribute sa = new SubjectAttribute();
        sa.setAttributeName(attributeName);

        sa.setStringValue(String.format("#%02x%02x%02x", color.getRed(),
                color.getGreen(), color.getBlue()));

        sa.setSubjectId(su.getId());
        sa.setInternalAttribute(true);
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
        if(session.getRobotSessionEventSourceId()!=null) {
            List<Subject> robotsList = subjectDao.listBySessionId(session.getRobotSessionEventSourceId());
            for(Subject su : robotsList){
                Subject robotClone = new Subject();
                robotClone.setSubjectDisplayName(su.getSubjectDisplayName());
                robotClone.setSubjectExternalId(su.getSubjectExternalId());//CONFLICT?
                robotClone.setSessionId(session.getId());
                robotClone = subjectDao.create(robotClone);
                subjectsInTeam.add(robotClone);
            }

        }
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
        synchronized (checkedInWaitingSubjectList) {
            checkedInWaitingSubjectList.put(subject.getSubjectExternalId(), subject);
            checkedInWaitingSubjectListById.put(subject.getId(), subject);

            if(this.session.isSessionPerpetual()){
                subjectHasSessionCheckInService.checkSubjectIn(subject, session);
            }
        }

    }

    private void setupTaskList(SessionWrapper sessionz) {
        List<Task> taskList = new ArrayList<>();
        List<SessionHasTaskGroup> taskGroupList = sessionService.listSessionHasTaskGroupBySessionId(sessionz.getId());


        if (sessionz.getTaskExecutionType().equals(TaskExecutionType.SEQUENTIAL_TASKGROUP_RANDOM_ORDER.getId().toString())) {
            Collections.shuffle(taskGroupList);
        }
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
        if (sessionz.getTaskExecutionType().equals(TaskExecutionType.SEQUENTIAL_RANDOM_ORDER.getId().toString())) {
            sessionz.randomizeTaskOrder();
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

    private void endSession() {
        session.setStatus(SessionStatus.DONE.getId().toString());
        sessionDao.update(session);
    }

    private void configureSession() {
        session.setStatus(SessionStatus.CONFIGURING.getId().toString());
        sessionDao.update(session);
    }

    @Override
    public void run() {
        if (session.getSessionScheduleType().equals(SessionScheduleType.PERPETUAL.getId().toString())
        || session.getSessionScheduleType().equals(SessionScheduleType.PERPETUAL_LANDING_PAGE.getId().toString())) {
            runPerpetual();
        } else {
            init();
            runSession();
        }
    }

    private String[] shouldSessionStartByConditionScript(Long scriptId, List<Subject> subjects) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        String[] subjectsToJoinSession = null;
        ExecutableScript es = executableScriptDao.get(scriptId);
        if(es!=null) {
            // Set JavaScript variables
            engine.put("subjects", teamService.getTeamatesJSONObject(subjects));

            Reader scriptReader = new InputStreamReader(new ByteArrayInputStream(
                    es.getScriptContent().getBytes()));

            try {
                engine.eval(scriptReader);

                String subjectsFromScript = (String) engine.getBindings(ScriptContext.ENGINE_SCOPE).get("subjectsToJoinSession");
                Boolean shouldStart = new Boolean(((Boolean) engine.getBindings(ScriptContext.ENGINE_SCOPE).get("shouldStart")));

                if (subjectsFromScript != null && shouldStart != null ) {
                    org.json.JSONArray array = new org.json.JSONArray(subjectsFromScript);
                    subjectsToJoinSession = new String[array.length()];
                    for(int i =0; i< array.length(); i++){
                        subjectsToJoinSession[i] = array.getString(i);
                    }

                    return subjectsToJoinSession;
                }
            } catch (ScriptException e) {
                return null;

            }
        }
        return null;

    }

    public void runPerpetual() {
        shouldRun = true;
        threadShouldStop = false;
        checkedInWaitingSubjectList = new HashMap<>();
        checkedInWaitingSubjectListById = new HashMap<>();
        sessionsRelatedToPerpetual = new HashMap<>();
        try {
            while (shouldRun) {

                Thread.sleep(1000);
                if ((checkedInWaitingSubjectListById.size() > 0)) {
                    if (session.getScheduleConditionType().equals(SessionScheduleConditionToStartType.NUMBER_OF_USERS_CHECKED_IN.getId().toString())) {
                        subjectCheckInList = subjectHasSessionCheckInService.listReadyToJoinSubjects(session.getId());
                        for (SubjectHasSessionCheckIn shscI : subjectCheckInList) {
                            if (subjectHasSessionCheckInService.hasSubjectExpiredOrNotPingedRecently(shscI)) {
                                checkedInWaitingSubjectListById.remove(shscI.getSubjectId());
                                continue;
                            }
                        }
                        if ((subjectCheckInList.size() >= session.getPerpetualSubjectsNumber())) {

                            synchronized (checkedInWaitingSubjectList) {
                                Session newSpawnedSession = this.sessionService.clonePerpetualSession(session);
                                List<Subject> subjectsInNewSession = new ArrayList<>();

                                int subjsInNewSession = 0;


                                for (SubjectHasSessionCheckIn shscI : subjectCheckInList) {


                                    if (subjsInNewSession <= session.getPerpetualSubjectsNumber()) {
                                        Long subjectId = shscI.getSubjectId();
                                        Subject su = subjectDao.get(subjectId);
//                                        if (su == null) {
//                                            su = new Subject();
//
//                                            su.setSessionId(newSpawnedSession.getId());
//                                            su.setSubjectExternalId(externalId);
//                                            su.setSubjectDisplayName(externalId);
//                                            su = subjectDao.create(su);
//                                        } else {
                                            su.setSessionId(newSpawnedSession.getId());
                                            subjectDao.update(su);
                                            checkedInWaitingSubjectListById.remove(su.getId());
                                            shscI.setJoinedSessionId(newSpawnedSession.getId());
                                            subjectHasSessionCheckInService.subjectJoinedSession(shscI);
                                        //}

                                        subjectsInNewSession.add(su);

                                        subjsInNewSession++;
                                    }
                                }
                                sessionsRelatedToPerpetual.put(newSpawnedSession.getId(), subjectsInNewSession);

                                subjectCommunicationService.createSubjectCommunications(newSpawnedSession.getId(), true);

                            }
                        }
                    } else {

                        if (session.getScheduleConditionType().equals(SessionScheduleConditionToStartType.CONDITION_SCRIPT.getId().toString())) {
                            subjectCheckInList = subjectHasSessionCheckInService.listReadyToJoinSubjects(session.getId());

                            for (SubjectHasSessionCheckIn shscI : subjectCheckInList) {

                                if (subjectHasSessionCheckInService.hasSubjectExpiredOrNotPingedRecently(shscI)) {
                                    checkedInWaitingSubjectListById.remove(shscI.getSubjectId());
                                }

                            }
                            List<Subject> checkedInSubjects = checkedInWaitingSubjectListById.values().stream().collect(Collectors.toList());
                            String[] subjectsToJoin = shouldSessionStartByConditionScript(session.getExecutableScriptId(), checkedInSubjects);
                            if (subjectsToJoin != null) {
                                for (String subjects : subjectsToJoin) {
                                    _log.debug("Subject to join: " + subjects);
                                }
                            }
                            if (subjectsToJoin != null && subjectsToJoin.length > 0) {
                                synchronized (checkedInWaitingSubjectList) {
                                    Session newSpawnedSession = this.sessionService.clonePerpetualSession(session);
                                    List<Subject> subjectsInNewSession = new ArrayList<>();



                                    for (SubjectHasSessionCheckIn shscI : subjectCheckInList) {


                                        String externalId = checkedInWaitingSubjectListById.get(shscI.getSubjectId()).getSubjectExternalId();
                                        for (String chosenExternalId : subjectsToJoin) {
                                            if (chosenExternalId.equals(externalId)) {
                                                Subject su = checkedInWaitingSubjectList.get(externalId);
                                                if (su != null) {
                                                    su.setSessionId(newSpawnedSession.getId());
                                                    subjectDao.update(su);
                                                    subjectsInNewSession.add(su);


                                                    checkedInWaitingSubjectListById.remove(su.getId());
                                                    shscI.setJoinedSessionId(newSpawnedSession.getId());
                                                    subjectHasSessionCheckInService.subjectJoinedSession(shscI);

                                                    continue;
                                                }
                                            }
                                        }
                                    }
                                    sessionsRelatedToPerpetual.put(newSpawnedSession.getId(), subjectsInNewSession);

                                    subjectCommunicationService.createSubjectCommunications(newSpawnedSession.getId(), true);


                                }
                            }
                        }
                    }

                }
                if (session.getSecondsRemainingForSession() < 0) {
                    shouldRun = false;
                }
                if (!sessionsRelatedToPerpetual.isEmpty()) {
                    sessionService.initializeSessionRunners();
                }
                List<Long> sessionsMigratedToRightCheckinList = new ArrayList<>();
                for (Long sessionId : sessionsRelatedToPerpetual.keySet()) {
                    SessionRunner sr = SessionRunnerManager.getSessionRunner(sessionId);

                    if (sr != null) {
                        for (Subject s : sessionsRelatedToPerpetual.get(sessionId)) {
                            sr.subjectCheckIn(s);
                            _log.debug("Checkin in: " + s.getSubjectExternalId() + " - session id: " + sr.getSession().getId());
                            perpetualSubjectsMigratedToSpawnedSessions.put(s.getSubjectExternalId());

                        }
                        sessionsMigratedToRightCheckinList.add(sessionId);
                    }
                }
                for (Long sess : sessionsMigratedToRightCheckinList) {
                    sessionsRelatedToPerpetual.remove(sess);
                }
            }
        }catch (InterruptedException ie){

        }
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

    public JSONArray getPerpetualSubjectsMigratedToSpawnedSessions() {
        return perpetualSubjectsMigratedToSpawnedSessions;
    }

    public void shouldStop() {
        this.threadShouldStop = true;
        this.shouldRun = false;
    }
}
