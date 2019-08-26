package edu.mit.cci.pogs.model.dao.executablescripthasresearchgroup;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScriptHasResearchGroup;


import java.util.List;

public interface ExecutableScriptHasResearchGroupDao extends Dao<ExecutableScriptHasResearchGroup, Long> {

    List<ExecutableScriptHasResearchGroup> list();
    List<ExecutableScriptHasResearchGroup> listByExecutableScriptId(Long executableScriptId);
    List<ExecutableScriptHasResearchGroup> listByResearchGroup(Long researchGroupId);
    void delete(ExecutableScriptHasResearchGroup rghau);

}
