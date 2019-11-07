package edu.mit.cci.pogs.model.dao.sessionexecutionattribute;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionExecutionAttribute;

public interface SessionExecutionAttributeDao extends Dao<SessionExecutionAttribute, Long> {

    List<SessionExecutionAttribute> listBySessionId(Long sessionId);
}
