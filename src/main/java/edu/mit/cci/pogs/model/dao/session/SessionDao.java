package edu.mit.cci.pogs.model.dao.session;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
 
public interface SessionDao extends Dao<Session, Long> {
 
    List<Session> list();

    List<Session> listByStudyId(Long conditionId);

    List<Session> listStartsIn(long initWindow);

    List<Session> listPerpetualCurrentlyAccpeting(long initWindow);
}
 
