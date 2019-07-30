package edu.mit.cci.pogs.model.dao.dictionary;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;

public interface DictionaryDao extends Dao<Dictionary, Long> {
    List<Dictionary> list();
    List<Dictionary> listDictionariesWithUserGroup(Long userId);
}
