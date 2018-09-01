package edu.mit.cci.pogs.model.dao.todoentry.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.todoentry.TodoEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntry;
import edu.mit.cci.pogs.model.jooq.tables.records.TodoEntryRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.TODO_ENTRY;

@Repository
public class TodoEntryDaoImpl extends AbstractDao<TodoEntry, Long, TodoEntryRecord> implements TodoEntryDao {
    private final DSLContext dslContext;

    @Autowired
    public TodoEntryDaoImpl(DSLContext dslContext) {

        super(dslContext, TODO_ENTRY, TODO_ENTRY.ID, TodoEntry.class);
        this.dslContext = dslContext;
    }

    public List<TodoEntry> list() {
        final SelectQuery<Record> query = dslContext.select()
                .from(TODO_ENTRY).getQuery();

        return query.fetchInto(TodoEntry.class);
    }

    public List<TodoEntry> listByCompletedTaskId(Long completedTaskId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(TODO_ENTRY).getQuery();
        query.addConditions(TODO_ENTRY.COMPLETED_TASK_ID.eq(completedTaskId));
        query.addConditions(TODO_ENTRY.DELETED_AT.isNull());
        query.addOrderBy(TODO_ENTRY.TODO_ENTRY_DATE);

        return query.fetchInto(TodoEntry.class);
    }

    public void delete(Long todoEntryId) {
        dslContext.delete(TODO_ENTRY)
                .where(TODO_ENTRY.ID.eq(todoEntryId))
                .execute();
    }
}
