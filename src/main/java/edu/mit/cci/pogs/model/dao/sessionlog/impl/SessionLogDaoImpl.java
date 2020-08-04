package edu.mit.cci.pogs.model.dao.sessionlog.impl;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.sessionlog.SessionLogDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionLog;
import edu.mit.cci.pogs.model.jooq.tables.records.SessionLogRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.SESSION_LOG;

@Repository
public class SessionLogDaoImpl extends AbstractDao<SessionLog, Long, SessionLogRecord> implements SessionLogDao {

    private final DSLContext dslContext;

    @Autowired
    public SessionLogDaoImpl(DSLContext dslContext) {
        super(dslContext, SESSION_LOG, SESSION_LOG.ID, SessionLog.class);
        this.dslContext = dslContext;
    }


}
