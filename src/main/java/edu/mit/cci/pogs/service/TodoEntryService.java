package edu.mit.cci.pogs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import edu.mit.cci.pogs.model.dao.todoentry.TodoEntryDao;
import edu.mit.cci.pogs.model.dao.todoentryassignment.TodoEntryAssignmentDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntryAssignment;

@Service
public class TodoEntryService {

    @Autowired
    private TodoEntryDao todoEntryDao;

    @Autowired
    private TodoEntryAssignmentDao todoEntryAssignmentDao;


    public void deleteTodoEntryByCompletedTaskId(Long completedTaskId) {
        for(TodoEntry te: todoEntryDao.listByCompletedTaskId(completedTaskId)){
            deleteTodoEntry(te.getId());
            todoEntryDao.delete(te.getId());
        }
    }

    private void deleteTodoEntry(Long id) {
        List<TodoEntryAssignment> todoEntryAssignmentList = todoEntryAssignmentDao
                .listByTodoEntryId(id,null);
        for(TodoEntryAssignment tea: todoEntryAssignmentList) {
            todoEntryAssignmentDao.delete(tea.getId());
        }
    }
}
