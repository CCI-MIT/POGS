package edu.mit.cci.pogs.model.dao.researchgroup;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroup;
 
public interface ResearchGroupDao extends Dao<ResearchGroup, Long> {
 
    List<ResearchGroup> list();
}
 
