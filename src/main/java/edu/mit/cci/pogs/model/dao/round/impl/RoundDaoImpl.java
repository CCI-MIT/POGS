package edu.mit.cci.pogs.model.dao.round.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.round.RoundDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.records.RoundRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
import static edu.mit.cci.pogs.model.jooq.Tables.ROUND;
 
@Repository
public class RoundDaoImpl extends AbstractDao<Round, Long, RoundRecord> implements RoundDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public RoundDaoImpl(DSLContext dslContext) {
        super(dslContext, ROUND, ROUND.ID, Round.class);
        this.dslContext = dslContext;
    }
 
    public List<Round> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(ROUND).getQuery();
 
        return query.fetchInto(Round.class);
    }

    public List<Round> listBySessionId(Long sessionId){

        final SelectQuery<Record> query = dslContext.select()
                .from(ROUND).getQuery();
        query.addConditions(ROUND.SESSION_ID.eq(sessionId));

        return query.fetchInto(Round.class);
    }

}
 
