package edu.mit.cci.pogs.model.dao.eventlog;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
 
public interface EventLogDao extends Dao<EventLog, Long> {
 
    List<EventLog> list();

    void deleteByCompletedTaskId(Long completedTaskId);
}
 