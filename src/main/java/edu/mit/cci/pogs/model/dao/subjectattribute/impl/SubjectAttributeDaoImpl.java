package edu.mit.cci.pogs.model.dao.subjectattribute.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.subjectattribute.SubjectAttributeDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.records.SubjectAttributeRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT_ATTRIBUTE;
import static edu.mit.cci.pogs.model.jooq.Tables.TEAM_HAS_SUBJECT;

@Repository
public class SubjectAttributeDaoImpl extends AbstractDao<SubjectAttribute, Long, SubjectAttributeRecord> implements SubjectAttributeDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public SubjectAttributeDaoImpl(DSLContext dslContext) {
        super(dslContext, SUBJECT_ATTRIBUTE, SUBJECT_ATTRIBUTE.ID, SubjectAttribute.class);
        this.dslContext = dslContext;
    }
 
    public List<SubjectAttribute> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT_ATTRIBUTE).getQuery();
 
        return query.fetchInto(SubjectAttribute.class);
    }

    public List<SubjectAttribute> listBySubjectId(Long subjectId){
        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT_ATTRIBUTE).getQuery();
        query.addConditions(SUBJECT_ATTRIBUTE.SUBJECT_ID.eq(subjectId));
        return query.fetchInto(SubjectAttribute.class);
    }

    public void deleteBySubjectId(Long subjectId) {
        dslContext.delete(SUBJECT_ATTRIBUTE)
                .where(SUBJECT_ATTRIBUTE.SUBJECT_ID.eq(subjectId))
                .execute();
    }
    public void delete(Long subjectAttributeId) {
        dslContext.delete(SUBJECT_ATTRIBUTE)
                .where(SUBJECT_ATTRIBUTE.ID.eq(subjectAttributeId))
                .execute();
    }
}
 
