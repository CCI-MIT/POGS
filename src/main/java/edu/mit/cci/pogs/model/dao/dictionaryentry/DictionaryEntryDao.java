package edu.mit.cci.pogs.model.dao.dictionaryentry;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;

public interface DictionaryEntryDao extends Dao<DictionaryEntry, Long> {

    List<DictionaryEntry> listDictionaryEntriesByDictionary(Long id);

    void deleteDictionaryEntry(DictionaryEntry dictionaryEntry);
}
