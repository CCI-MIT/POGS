package edu.mit.cci.pogs.model.dao.sessionlog.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.sessionlog.SessionLogDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.records.SessionLogRecord;
import edu.mit.cci.pogs.utils.DateUtils;

import static edu.mit.cci.pogs.model.jooq.Tables.SESSION_LOG;
import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT;

@Repository
public class SessionLogDaoImpl extends AbstractDao<SessionLog, Long, SessionLogRecord> implements SessionLogDao {

    private final DSLContext dslContext;

    @Autowired
    public SessionLogDaoImpl(DSLContext dslContext) {
        super(dslContext, SESSION_LOG, SESSION_LOG.ID, SessionLog.class);
        this.dslContext = dslContext;
    }

    public List<SessionLog> listTodayLogs(Long sessionId){

            final SelectQuery<Record> query = dslContext.select()
                    .from(SESSION_LOG).getQuery();
            query.addConditions(SESSION_LOG.SESSION_ID.eq(sessionId));
            query.addConditions(SESSION_LOG.LOG_TIME.greaterThan(new Timestamp(DateUtils.today())));

            return query.fetchInto(SessionLog.class);

    }

}
