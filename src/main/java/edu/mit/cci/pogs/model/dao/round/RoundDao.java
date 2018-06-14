package edu.mit.cci.pogs.model.dao.round;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
 
public interface RoundDao extends Dao<Round, Long> {
 
    List<Round> list();
    List<Round> listBySessionId(Long sessionId);
}
 
