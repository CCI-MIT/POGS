package edu.mit.cci.pogs.runner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SessionRunnerManager {

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
                liveRunners.put(sessionId, sessionRunner);
            }
        }
    }

    public static void removeSessionRunner(Long sessionId) {
        synchronized (liveRunners) {
            if (liveRunners.get(sessionId) != null) {
                liveRunners.get(sessionId).setShouldRun(false);
                liveRunners.remove(sessionId);
            }
        }
    }
}
