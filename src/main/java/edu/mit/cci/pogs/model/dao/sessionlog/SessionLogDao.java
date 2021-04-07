package edu.mit.cci.pogs.model.dao.sessionlog;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionLog;

public interface SessionLogDao extends Dao<SessionLog, Long> {

    List<SessionLog> listTodayLogs(Long sessionId);
}
