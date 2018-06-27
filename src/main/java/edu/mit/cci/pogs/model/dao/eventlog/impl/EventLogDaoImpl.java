package edu.mit.cci.pogs.model.dao.eventlog.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.records.EventLogRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
import static edu.mit.cci.pogs.model.jooq.Tables.EVENT_LOG;
 
@Repository
public class EventLogDaoImpl extends AbstractDao<EventLog, Long, EventLogRecord> implements EventLogDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public EventLogDaoImpl(DSLContext dslContext) {
        super(dslContext, EVENT_LOG, EVENT_LOG.ID, EventLog.class);
        this.dslContext = dslContext;
    }
 
    public List<EventLog> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(EVENT_LOG).getQuery();
 
        return query.fetchInto(EventLog.class);
    }
    public void deleteByCompletedTaskId(Long completedTaskId){
        dslContext.delete(EVENT_LOG)
                .where(EVENT_LOG.COMPLETED_TASK_ID.eq(completedTaskId))
                .execute();
    }
 
}
 
