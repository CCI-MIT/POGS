package edu.mit.cci.pogs.model.dao.teamhassubject.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.teamhassubject.TeamHasSubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TeamHasSubject;
import edu.mit.cci.pogs.model.jooq.tables.records.TeamHasSubjectRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
import static edu.mit.cci.pogs.model.jooq.Tables.TEAM_HAS_SUBJECT;
 
@Repository
public class TeamHasSubjectDaoImpl extends AbstractDao<TeamHasSubject, Long, TeamHasSubjectRecord> implements TeamHasSubjectDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public TeamHasSubjectDaoImpl(DSLContext dslContext) {
        super(dslContext, TEAM_HAS_SUBJECT, TEAM_HAS_SUBJECT.ID, TeamHasSubject.class);
        this.dslContext = dslContext;
    }
 
    public List<TeamHasSubject> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(TEAM_HAS_SUBJECT).getQuery();
 
        return query.fetchInto(TeamHasSubject.class);
    }
    public List<TeamHasSubject> listByTeamId(Long teamId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(TEAM_HAS_SUBJECT).getQuery();
        query.addConditions(TEAM_HAS_SUBJECT.TEAM_ID.eq(teamId));

        return query.fetchInto(TeamHasSubject.class);
    }

    public void deleteByTeamId(Long teamId){
        dslContext.delete(TEAM_HAS_SUBJECT)
                .where(TEAM_HAS_SUBJECT.TEAM_ID.eq(teamId))
                .execute();
    }

 
}
 
