package edu.mit.cci.pogs.model.dao.todoentryassignment;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntryAssignment;

import java.util.List;

public interface TodoEntryAssignmentDao extends Dao<TodoEntryAssignment, Long> {

    List<TodoEntryAssignment> get();
    TodoEntryAssignment getByTodoEntryIdSubjectId(Long todoEntryId, Long subjectId);
    List<TodoEntryAssignment> listByTodoEntryId(Long todoEntryId, boolean currentlyAssigned);
}

