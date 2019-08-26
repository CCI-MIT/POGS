package edu.mit.cci.pogs.model.dao.dictionaryhasresearchgroup;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryHasResearchGroup;

import java.util.List;

public interface DictionaryHasResearchGroupDao extends Dao<DictionaryHasResearchGroup, Long> {

    List<DictionaryHasResearchGroup> list();
    List<DictionaryHasResearchGroup> listByDictionaryId(Long dictionaryId);
    List<DictionaryHasResearchGroup> listByResearchGroup(Long researchGroupId);
    void delete(DictionaryHasResearchGroup rghau);
}
