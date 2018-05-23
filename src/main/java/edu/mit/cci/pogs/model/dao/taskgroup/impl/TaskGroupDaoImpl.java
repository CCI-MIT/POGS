package edu.mit.cci.pogs.model.dao.taskgroup.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.taskgroup.TaskGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.records.TaskGroupRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.STUDY_HAS_RESEARCH_GROUP;
import static edu.mit.cci.pogs.model.jooq.Tables.TASK_GROUP;
 
@Repository
public class TaskGroupDaoImpl extends AbstractDao<TaskGroup, Long, TaskGroupRecord> implements TaskGroupDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public TaskGroupDaoImpl(DSLContext dslContext) {
        super(dslContext, TASK_GROUP, TASK_GROUP.ID, TaskGroup.class);
        this.dslContext = dslContext;
    }
 
    public List<TaskGroup> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_GROUP).getQuery();
 
        return query.fetchInto(TaskGroup.class);
    }
}
 
