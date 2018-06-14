package edu.mit.cci.pogs.model.dao.teamhassubject;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TeamHasSubject;
 
public interface TeamHasSubjectDao extends Dao<TeamHasSubject, Long> {
 
    List<TeamHasSubject> list();

    List<TeamHasSubject> listByTeamId(Long teamId);

}
 
