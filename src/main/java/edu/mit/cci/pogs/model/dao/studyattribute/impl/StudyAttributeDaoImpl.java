package edu.mit.cci.pogs.model.dao.studyattribute.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.studyattribute.StudyAttributeDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyAttribute;
import edu.mit.cci.pogs.model.jooq.tables.records.StudyAttributeRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.STUDY_ATTRIBUTE;

@Repository

public class StudyAttributeDaoImpl extends AbstractDao<StudyAttribute, Long, StudyAttributeRecord> implements StudyAttributeDao {
    private final DSLContext dslContext;

    @Autowired
    public StudyAttributeDaoImpl(DSLContext dslContext) {
        super(dslContext, STUDY_ATTRIBUTE, STUDY_ATTRIBUTE.ID, StudyAttribute.class);
        this.dslContext = dslContext;
    }

    public List<StudyAttribute> listByStudyId(Long studyId){
        final SelectQuery<Record> query = dslContext.select()
                .from(STUDY_ATTRIBUTE).getQuery();
        query.addConditions(STUDY_ATTRIBUTE.STUDY_ID.eq(studyId));
        return query.fetchInto(StudyAttribute.class);
    }

    public List<StudyAttribute> listBySessionId(Long sessionId){
        final SelectQuery<Record> query = dslContext.select()
                .from(STUDY_ATTRIBUTE).getQuery();
        query.addConditions(STUDY_ATTRIBUTE.STUDY_ID.eq(sessionId));
        return query.fetchInto(StudyAttribute.class);
    }

}
