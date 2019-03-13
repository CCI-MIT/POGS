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

import static edu.mit.cci.pogs.model.jooq.Tables.EXECUTABLE_SCRIPT;

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

}
