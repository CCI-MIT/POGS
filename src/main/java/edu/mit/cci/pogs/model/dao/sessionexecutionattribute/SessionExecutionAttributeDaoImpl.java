package edu.mit.cci.pogs.model.dao.sessionexecutionattribute;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionExecutionAttribute;
import edu.mit.cci.pogs.model.jooq.tables.records.SessionExecutionAttributeRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.SESSION_EXECUTION_ATTRIBUTE;

@Repository
public class SessionExecutionAttributeDaoImpl extends AbstractDao<SessionExecutionAttribute, Long, SessionExecutionAttributeRecord> implements SessionExecutionAttributeDao {

    private final DSLContext dslContext;

    @Autowired
    public SessionExecutionAttributeDaoImpl(DSLContext dslContext) {
        super(dslContext, SESSION_EXECUTION_ATTRIBUTE, SESSION_EXECUTION_ATTRIBUTE.ID, SessionExecutionAttribute.class);
        this.dslContext = dslContext;
    }

    public List<SessionExecutionAttribute> listBySessionId(Long sessionId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(SESSION_EXECUTION_ATTRIBUTE).getQuery();
        query.addConditions(SESSION_EXECUTION_ATTRIBUTE.SESSION_ID.eq(sessionId));

        return query.fetchInto(SessionExecutionAttribute.class);
    }
}
