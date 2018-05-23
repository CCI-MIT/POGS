package edu.mit.cci.pogs.model.dao.sessionhastaskgroup.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.sessionhastaskgroup.SessionHasTaskGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.records.SessionHasTaskGroupRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.SESSION_HAS_TASK_GROUP;

@Repository
public class SessionHasTaskGroupDaoImpl extends AbstractDao<SessionHasTaskGroup, Long, SessionHasTaskGroupRecord> implements SessionHasTaskGroupDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public SessionHasTaskGroupDaoImpl(DSLContext dslContext) {
        super(dslContext, SESSION_HAS_TASK_GROUP, SESSION_HAS_TASK_GROUP.ID, SessionHasTaskGroup.class);
        this.dslContext = dslContext;
    }
 
    public List<SessionHasTaskGroup> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(SESSION_HAS_TASK_GROUP).getQuery();
 
        return query.fetchInto(SessionHasTaskGroup.class);
    }

    public List<SessionHasTaskGroup> listSessionHasTaskGroupBySessionId(Long sessionId){
        final SelectQuery<Record> query = dslContext.select()
                .from(SESSION_HAS_TASK_GROUP).getQuery();

        query.addConditions(SESSION_HAS_TASK_GROUP.SESSION_ID.eq(sessionId));

        return query.fetchInto(SessionHasTaskGroup.class);
    }

    public void delete(SessionHasTaskGroup rghau) {
        dslContext.delete(SESSION_HAS_TASK_GROUP)
                .where(SESSION_HAS_TASK_GROUP.ID.eq(rghau.getId()))
                .execute();

    }
}
 
