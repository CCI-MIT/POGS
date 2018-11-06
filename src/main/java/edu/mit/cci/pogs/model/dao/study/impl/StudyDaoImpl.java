package edu.mit.cci.pogs.model.dao.study.impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.model.jooq.tables.records.StudyRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.RESEARCH_GROUP_HAS_AUTH_USER;
import static edu.mit.cci.pogs.model.jooq.Tables.STUDY;
import static edu.mit.cci.pogs.model.jooq.Tables.STUDY_HAS_RESEARCH_GROUP;

@Repository
public class StudyDaoImpl extends AbstractDao<Study, Long, StudyRecord> implements StudyDao {

    private final DSLContext dslContext;

    @Autowired
    public StudyDaoImpl(DSLContext dslContext) {
        super(dslContext, STUDY, STUDY.ID, Study.class);
        this.dslContext = dslContext;
    }


    public List<Study> listStudiesWithUserGroup(Long userId){

        final SelectQuery<Record> query = dslContext.selectDistinct(STUDY.fields())
                .from(STUDY)
                .join(STUDY_HAS_RESEARCH_GROUP).on(STUDY_HAS_RESEARCH_GROUP.STUDY_ID.eq(STUDY.ID))
                .join(RESEARCH_GROUP_HAS_AUTH_USER).on(RESEARCH_GROUP_HAS_AUTH_USER.RESEARCH_GROUP_ID.eq(STUDY_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID))
                .where(RESEARCH_GROUP_HAS_AUTH_USER.AUTH_USER_ID.eq(userId))
                .getQuery();

        return query.fetchInto(Study.class);
    }

}


