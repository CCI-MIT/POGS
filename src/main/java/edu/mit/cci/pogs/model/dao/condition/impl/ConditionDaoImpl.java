package edu.mit.cci.pogs.model.dao.condition.impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.condition.ConditionDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Condition;
import edu.mit.cci.pogs.model.jooq.tables.records.ConditionRecord;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.CONDITION;

@Repository
public class ConditionDaoImpl extends AbstractDao<Condition, Long, ConditionRecord> implements ConditionDao {

    private final DSLContext dslContext;

    @Autowired
    public ConditionDaoImpl(DSLContext dslContext) {
        super(dslContext, CONDITION, CONDITION.ID, Condition.class);
        this.dslContext = dslContext;
    }

    public List<Condition> list() {

        final SelectQuery<Record> query = dslContext.select()
                .from(CONDITION).getQuery();

        return query.fetchInto(Condition.class);
    }

    public List<Condition> listByStudyId(Long studyId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(CONDITION).getQuery();
        query.addConditions(CONDITION.STUDY_ID.eq(studyId));

        return query.fetchInto(Condition.class);
    }

}
 
