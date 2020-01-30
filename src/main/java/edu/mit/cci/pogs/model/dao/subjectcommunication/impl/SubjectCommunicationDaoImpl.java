package edu.mit.cci.pogs.model.dao.subjectcommunication.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.subjectcommunication.SubjectCommunicationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectCommunication;
import edu.mit.cci.pogs.model.jooq.tables.records.SubjectCommunicationRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT_ATTRIBUTE;
import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT_COMMUNICATION;
 
@Repository
public class SubjectCommunicationDaoImpl extends AbstractDao<SubjectCommunication, Long, SubjectCommunicationRecord> implements SubjectCommunicationDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public SubjectCommunicationDaoImpl(DSLContext dslContext) {
        super(dslContext, SUBJECT_COMMUNICATION, SUBJECT_COMMUNICATION.ID, SubjectCommunication.class);
        this.dslContext = dslContext;
    }
 
    public List<SubjectCommunication> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT_COMMUNICATION).getQuery();
 
        return query.fetchInto(SubjectCommunication.class);
    }

    public List<SubjectCommunication> listByFromSubjectId(Long fromSubjectId){

        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT_COMMUNICATION).getQuery();
                query.addConditions(SUBJECT_COMMUNICATION.FROM_SUBJECT_ID.eq(fromSubjectId));
                query.addOrderBy(SUBJECT_COMMUNICATION.TO_SUBJECT_ID);

        return query.fetchInto(SubjectCommunication.class);
    }

    public void deleteBySubjectId(Long subjectId) {
        dslContext.delete(SUBJECT_COMMUNICATION)
                .where(SUBJECT_COMMUNICATION.FROM_SUBJECT_ID.eq(subjectId))
                .or(SUBJECT_COMMUNICATION.TO_SUBJECT_ID.eq(subjectId))
                .execute();
    }

}
 
