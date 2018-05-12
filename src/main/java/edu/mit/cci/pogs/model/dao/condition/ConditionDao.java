package edu.mit.cci.pogs.model.dao.condition;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Condition;
 
public interface ConditionDao extends Dao<Condition, Long> {
 
    List<Condition> list();
}
 
