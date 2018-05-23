package edu.mit.cci.pogs.model.dao.session.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.records.SessionRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
import static edu.mit.cci.pogs.model.jooq.Tables.SESSION;
 
@Repository
public class SessionDaoImpl extends AbstractDao<Session, Long, SessionRecord> implements SessionDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public SessionDaoImpl(DSLContext dslContext) {
        super(dslContext, SESSION, SESSION.ID, Session.class);
        this.dslContext = dslContext;
    }
 
    public List<Session> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(SESSION).getQuery();
 
        return query.fetchInto(Session.class);
    }
    public List<Session> listByConditionId(Long conditionId){
        final SelectQuery<Record> query = dslContext.select()
                .from(SESSION).getQuery();

        query.addConditions(SESSION.CONDITION_ID.eq(conditionId));
        return query.fetchInto(Session.class);
    }

}
 
