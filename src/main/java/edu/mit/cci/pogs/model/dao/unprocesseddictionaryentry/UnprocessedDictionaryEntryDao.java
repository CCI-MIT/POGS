package edu.mit.cci.pogs.model.dao.unprocesseddictionaryentry;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.UnprocessedDictionaryEntry;

public interface UnprocessedDictionaryEntryDao extends Dao<UnprocessedDictionaryEntry, Long> {

    List<UnprocessedDictionaryEntry> listDictionaryEntriesByDictionary(Long id);
    void deleteDictionaryEntry(UnprocessedDictionaryEntry dictionaryEntry);

}
