package edu.mit.cci.pogs.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SessionRunnerManager {

    private static final Logger _log = LoggerFactory.getLogger(SessionRunnerManager.class);


    public static Collection<SessionRunner> getLiveRunners() {
        synchronized (liveRunners) {
            return liveRunners.values();
        }
    }

    private static Map<Long, SessionRunner> liveRunners = new HashMap<>();

    public static SessionRunner getSessionRunner(Long sessionId) {
        synchronized (liveRunners) {
            return liveRunners.get(sessionId);
        }
    }

    public static void addSessionRunner(Long sessionId, SessionRunner sessionRunner) {
        synchronized (liveRunners) {
            if (liveRunners.get(sessionId) == null) {
                _log.info("Adding new session runner for sessionId : "+ sessionId);
                liveRunners.put(sessionId, sessionRunner);
            }
        }
    }

    public static void removeSessionRunner(Long sessionId) {
        synchronized (liveRunners) {
            _log.info("Removing session runner for sessionId : "+ sessionId);
            if (liveRunners.get(sessionId) != null) {
                liveRunners.get(sessionId).setShouldRun(false);
                liveRunners.remove(sessionId);
            }
        }
    }
}
