package edu.mit.cci.pogs.model.dao.executablescript;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;


public interface ExecutableScriptDao  extends Dao<ExecutableScript, Long> {

    List<ExecutableScript> listByScriptType(ScriptType scriptType);

    List<ExecutableScript> listExecutableScriptsWithUserGroup(Long userId);

    List<ExecutableScript> list();



}
