package edu.mit.cci.pogs.model.dao.todoentry;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntry;

import java.util.List;

public interface TodoEntryDao extends Dao<TodoEntry, Long> {

    List<TodoEntry> list();
    List<TodoEntry> listByCompletedTaskId(Long completedTaskId);
}
