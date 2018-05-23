package edu.mit.cci.pogs.model.dao.sessionhastaskgroup;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
 
public interface SessionHasTaskGroupDao extends Dao<SessionHasTaskGroup, Long> {
 
    List<SessionHasTaskGroup> list();
    List<SessionHasTaskGroup> listSessionHasTaskGroupBySessionId(Long sessionId);
    void delete(SessionHasTaskGroup rghau);
}
 
