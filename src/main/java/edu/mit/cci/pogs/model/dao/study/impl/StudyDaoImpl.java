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

import static edu.mit.cci.pogs.model.jooq.Tables.STUDY;

@Repository
public class StudyDaoImpl extends AbstractDao<Study, Long, StudyRecord> implements StudyDao {

    private final DSLContext dslContext;

    @Autowired
    public StudyDaoImpl(DSLContext dslContext) {
        super(dslContext, STUDY, STUDY.ID, Study.class);
        this.dslContext = dslContext;
    }


    public List<Study> listStudiesWithUserGroup(){

        final SelectQuery<Record> query = dslContext.select()
                .from(STUDY).getQuery();

        return query.fetchInto(Study.class);
    }

}


