package edu.mit.cci.pogs.model.dao.subject.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.records.SubjectRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT;
 
@Repository
public class SubjectDaoImpl extends AbstractDao<Subject, Long, SubjectRecord> implements SubjectDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public SubjectDaoImpl(DSLContext dslContext) {
        super(dslContext, SUBJECT, SUBJECT.ID, Subject.class);
        this.dslContext = dslContext;
    }
 
    public List<Subject> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT).getQuery();
 
        return query.fetchInto(Subject.class);
    }
    public List<Subject> listBySessionId( Long sessionId){
        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT).getQuery();
        query.addConditions(SUBJECT.SESSION_ID.eq(sessionId));
        return query.fetchInto(Subject.class);
    }
 
}
 
