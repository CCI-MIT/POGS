package edu.mit.cci.pogs.model.dao.todoentryassignment.impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.todoentryassignment.TodoEntryAssignmentDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntryAssignment;
import edu.mit.cci.pogs.model.jooq.tables.records.TodoEntryAssignmentRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.TODO_ENTRY;
import static edu.mit.cci.pogs.model.jooq.Tables.TODO_ENTRY_ASSIGNMENT;

@Repository
public class TodoEntryAssignmentDaoImpl extends AbstractDao<TodoEntryAssignment, Long, TodoEntryAssignmentRecord> implements TodoEntryAssignmentDao {

    private final DSLContext dslContext;

    @Autowired
    public TodoEntryAssignmentDaoImpl(DSLContext dslContext) {
        super(dslContext, TODO_ENTRY_ASSIGNMENT, TODO_ENTRY_ASSIGNMENT.ID, TodoEntryAssignment.class);
        this.dslContext = dslContext;
    }

    public List<TodoEntryAssignment> get(){
        final SelectQuery<Record> query = dslContext.select()
                .from(TODO_ENTRY_ASSIGNMENT).getQuery();

        return query.fetchInto(TodoEntryAssignment.class);
    }

    public TodoEntryAssignment getByTodoEntryIdSubjectId(Long todoEntryId, Long subjectId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(TODO_ENTRY_ASSIGNMENT).getQuery();
        query.addConditions(TODO_ENTRY_ASSIGNMENT.TODO_ENTRY_ID.eq(todoEntryId));
        query.addConditions(TODO_ENTRY_ASSIGNMENT.SUBJECT_ID.eq(subjectId));

        Record record =  query.fetchOne();
        if(record == null) {
            return null;
        }else{
            return record.into(TodoEntryAssignment.class);
        }

    }


    public List<TodoEntryAssignment> listByTodoEntryId(Long todoEntryId, boolean currentlyAssigned) {
        final SelectQuery<Record> query = dslContext.select()
                .from(TODO_ENTRY_ASSIGNMENT).getQuery();
        query.addConditions(TODO_ENTRY_ASSIGNMENT.TODO_ENTRY_ID.eq(todoEntryId));
        query.addConditions(TODO_ENTRY_ASSIGNMENT.CURRENT_ASSIGNED.eq(currentlyAssigned));
        return query.fetchInto(TodoEntryAssignment.class);
    }
}
