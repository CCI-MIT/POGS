package edu.mit.cci.pogs.model.dao.completedtaskattribute;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
 
public interface CompletedTaskAttributeDao extends Dao<CompletedTaskAttribute, Long> {
 
    List<CompletedTaskAttribute> list();

    CompletedTaskAttribute getByAttributeNameCompletedTaskId(String attributeName, Long completedTaskId);
}
 
