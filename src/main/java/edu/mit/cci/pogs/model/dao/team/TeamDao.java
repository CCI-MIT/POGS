package edu.mit.cci.pogs.model.dao.team;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
 
public interface TeamDao extends Dao<Team, Long> {
 
    List<Team> list();

    List<Team> listByRoundId(Long roundId);

    Team getByRoundIdTaskId(Long roundId, Long taskId);
}
 
