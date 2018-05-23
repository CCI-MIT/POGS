package edu.mit.cci.pogs.model.dao.taskhasresearchgroup.impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.taskhasresearchgroup.TaskHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.records.TaskHasResearchGroupRecord;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.STUDY_HAS_RESEARCH_GROUP;
import static edu.mit.cci.pogs.model.jooq.Tables.TASK_HAS_RESEARCH_GROUP;

@Repository
public class TaskHasResearchGroupDaoImpl extends AbstractDao<TaskHasResearchGroup, Long, TaskHasResearchGroupRecord> implements TaskHasResearchGroupDao {

    private final DSLContext dslContext;

    @Autowired
    public TaskHasResearchGroupDaoImpl(DSLContext dslContext) {
        super(dslContext, TASK_HAS_RESEARCH_GROUP, TASK_HAS_RESEARCH_GROUP.ID, TaskHasResearchGroup.class);
        this.dslContext = dslContext;
    }

    public List<TaskHasResearchGroup> list() {

        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_HAS_RESEARCH_GROUP).getQuery();

        return query.fetchInto(TaskHasResearchGroup.class);
    }


    public List<TaskHasResearchGroup> listByTaskId(Long taskId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(TASK_HAS_RESEARCH_GROUP).getQuery();
        query.addConditions(TASK_HAS_RESEARCH_GROUP.TASK_ID.eq(taskId));
        return query.fetchInto(TaskHasResearchGroup.class);
    }

    public void delete(TaskHasResearchGroup rghau) {
        dslContext.delete(TASK_HAS_RESEARCH_GROUP)
                .where(TASK_HAS_RESEARCH_GROUP.ID.eq(rghau.getId()))
                .execute();

    }
}
 
