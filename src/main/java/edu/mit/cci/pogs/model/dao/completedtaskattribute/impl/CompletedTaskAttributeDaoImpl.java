package edu.mit.cci.pogs.model.dao.completedtaskattribute.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.completedtaskattribute.CompletedTaskAttributeDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.records.CompletedTaskAttributeRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.COMPLETED_TASK_ATTRIBUTE;

@Repository
public class CompletedTaskAttributeDaoImpl extends AbstractDao<CompletedTaskAttribute, Long, CompletedTaskAttributeRecord> implements CompletedTaskAttributeDao {

    private final DSLContext dslContext;

    @Autowired
    public CompletedTaskAttributeDaoImpl(DSLContext dslContext) {
        super(dslContext, COMPLETED_TASK_ATTRIBUTE, COMPLETED_TASK_ATTRIBUTE.ID, CompletedTaskAttribute.class);
        this.dslContext = dslContext;
    }

    public List<CompletedTaskAttribute> list() {

        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK_ATTRIBUTE).getQuery();

        return query.fetchInto(CompletedTaskAttribute.class);
    }

    public List<CompletedTaskAttribute> listByAttributePrefix(String attributePrefix,
                                                              Long completedTaskId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK_ATTRIBUTE).getQuery();

        query.addConditions(COMPLETED_TASK_ATTRIBUTE.ATTRIBUTE_NAME.like(attributePrefix+"%"));
        query.addConditions(COMPLETED_TASK_ATTRIBUTE.COMPLETED_TASK_ID.eq(completedTaskId));

        return query.fetchInto(CompletedTaskAttribute.class);
    }

    public List<CompletedTaskAttribute> listByCompletedTaskId(Long completedTaskId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK_ATTRIBUTE).getQuery();
        query.addConditions(COMPLETED_TASK_ATTRIBUTE.COMPLETED_TASK_ID.eq(completedTaskId));

        return query.fetchInto(CompletedTaskAttribute.class);
    }


    public CompletedTaskAttribute getByAttributeNameCompletedTaskId(String attributeName,
                                                                    Long completedTaskId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK_ATTRIBUTE).getQuery();

        query.addConditions(COMPLETED_TASK_ATTRIBUTE.ATTRIBUTE_NAME.eq(attributeName));
        query.addConditions(COMPLETED_TASK_ATTRIBUTE.COMPLETED_TASK_ID.eq(completedTaskId));

        List<CompletedTaskAttribute> result = query.fetchInto(CompletedTaskAttribute.class);
        if (result == null || result.size() == 0) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public List<CompletedTaskAttribute> listByCompletedTasksIds(List<Long> completedTaskIds){
        final SelectQuery<Record> query = dslContext.select()
                .from(COMPLETED_TASK_ATTRIBUTE).where(COMPLETED_TASK_ATTRIBUTE.
                        COMPLETED_TASK_ID.in(completedTaskIds)).getQuery();
        return query.fetchInto(CompletedTaskAttribute.class);
    }

    public void deleteByCompletedTaskId(Long completedTaskId) {
        dslContext.delete(COMPLETED_TASK_ATTRIBUTE)
                .where(COMPLETED_TASK_ATTRIBUTE.COMPLETED_TASK_ID.eq(completedTaskId))
                .execute();

    }


}
 
