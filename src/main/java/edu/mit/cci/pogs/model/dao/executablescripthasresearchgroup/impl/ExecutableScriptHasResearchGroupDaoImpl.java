package edu.mit.cci.pogs.model.dao.executablescripthasresearchgroup.impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.executablescripthasresearchgroup.ExecutableScriptHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScriptHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.records.ExecutableScriptHasResearchGroupRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static edu.mit.cci.pogs.model.jooq.Tables.EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP;

import java.util.List;

@Repository
public class ExecutableScriptHasResearchGroupDaoImpl extends AbstractDao<ExecutableScriptHasResearchGroup, Long, ExecutableScriptHasResearchGroupRecord> implements ExecutableScriptHasResearchGroupDao {

    private final DSLContext dslContext;

    @Autowired
    public ExecutableScriptHasResearchGroupDaoImpl(DSLContext dslContext) {
        super(dslContext, EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP, EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP.ID, ExecutableScriptHasResearchGroup.class);
        this.dslContext = dslContext;
    }

    public List<ExecutableScriptHasResearchGroup> list(){

        final SelectQuery<Record> query = dslContext.select()
                .from(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP).getQuery();

        return query.fetchInto(ExecutableScriptHasResearchGroup.class);
    }

    public List<ExecutableScriptHasResearchGroup> listByExecutableScriptId(Long executableScriptId){

        final SelectQuery<Record> query = dslContext.select()
                .from(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP).getQuery();
        query.addConditions(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP.EXECUTABLE_SCRIPT_ID.eq(executableScriptId));
        return query.fetchInto(ExecutableScriptHasResearchGroup.class);
    }

    public List<ExecutableScriptHasResearchGroup> listByResearchGroup(Long researchGroupId){

        final SelectQuery<Record> query = dslContext.select()
                .from(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP).getQuery();
        query.addConditions(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID.eq(researchGroupId));
        return query.fetchInto(ExecutableScriptHasResearchGroup.class);
    }

    public void delete(ExecutableScriptHasResearchGroup rghau) {
        dslContext.delete(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP)
                .where(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP.ID.eq(rghau.getId()))
                .execute();

    }
}
