package edu.mit.cci.pogs.model.dao.executablescript.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.dao.executablescript.ScriptType;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.model.jooq.tables.records.ExecutableScriptRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.*;

@Repository
public class ExecutableScriptDaoImpl extends AbstractDao<ExecutableScript, Long, ExecutableScriptRecord> implements ExecutableScriptDao {

    private final DSLContext dslContext;

    @Autowired
    public ExecutableScriptDaoImpl(DSLContext dslContext) {
        super(dslContext, EXECUTABLE_SCRIPT, EXECUTABLE_SCRIPT.ID, ExecutableScript.class);
        this.dslContext = dslContext;
    }


    public List<ExecutableScript> listByScriptType(ScriptType scriptType) {

            final SelectQuery<Record> query = dslContext.select()
                    .from(EXECUTABLE_SCRIPT).getQuery();

            query.addConditions(EXECUTABLE_SCRIPT.SCRIPT_TYPE.eq(scriptType.getId().toString()));

            return query.fetchInto(ExecutableScript.class);

    }


    public List<ExecutableScript> listByScriptTypeWithUserGroup(ScriptType scriptType,Long userId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(EXECUTABLE_SCRIPT)
                .join(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP).on(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP
                        .EXECUTABLE_SCRIPT_ID.eq(EXECUTABLE_SCRIPT.ID))
                .join(RESEARCH_GROUP_HAS_AUTH_USER).on(RESEARCH_GROUP_HAS_AUTH_USER
                        .RESEARCH_GROUP_ID.eq(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP
                                .RESEARCH_GROUP_ID)).getQuery();

        query.addConditions(RESEARCH_GROUP_HAS_AUTH_USER.AUTH_USER_ID.eq(userId));
        query.addConditions(EXECUTABLE_SCRIPT.SCRIPT_TYPE.eq(scriptType.getId().toString()));

        return query.fetchInto(ExecutableScript.class);

    }

    @Override
    public List<ExecutableScript> listExecutableScriptsWithUserGroup(Long userId) {
        final SelectQuery<Record> query = dslContext.select(EXECUTABLE_SCRIPT.fields())
                .from(EXECUTABLE_SCRIPT)
                .join(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP).on(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP
                        .EXECUTABLE_SCRIPT_ID.eq(EXECUTABLE_SCRIPT.ID))
                .join(RESEARCH_GROUP_HAS_AUTH_USER).on(RESEARCH_GROUP_HAS_AUTH_USER
                        .RESEARCH_GROUP_ID.eq(EXECUTABLE_SCRIPT_HAS_RESEARCH_GROUP
                                .RESEARCH_GROUP_ID))
                .where(RESEARCH_GROUP_HAS_AUTH_USER.AUTH_USER_ID.eq(userId))
                .getQuery();

        return query.fetchInto(ExecutableScript.class);
    }

    @Override
    public List<ExecutableScript> list() {
        final SelectQuery<Record> query = dslContext.select()
                .from(EXECUTABLE_SCRIPT).getQuery();



        return query.fetchInto(ExecutableScript.class);
    }
}
