package edu.mit.cci.pogs.model.dao.team.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.model.jooq.tables.records.TeamRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
import static edu.mit.cci.pogs.model.jooq.Tables.TEAM;
 
@Repository
public class TeamDaoImpl extends AbstractDao<Team, Long, TeamRecord> implements TeamDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public TeamDaoImpl(DSLContext dslContext) {
        super(dslContext, TEAM, TEAM.ID, Team.class);
        this.dslContext = dslContext;
    }
 
    public List<Team> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(TEAM).getQuery();
 
        return query.fetchInto(Team.class);
    }

    public List<Team> listByRoundId(Long id){
        final SelectQuery<Record> query = dslContext.select()
                .from(TEAM).getQuery();
        query.addConditions(TEAM.ROUND_ID.eq(id));

        return query.fetchInto(Team.class);
    }
 
}
 
