package edu.mit.cci.pogs.model.dao.unprocesseddictionaryentry.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.unprocesseddictionaryentry.UnprocessedDictionaryEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.UnprocessedDictionaryEntry;
import edu.mit.cci.pogs.model.jooq.tables.records.UnprocessedDictionaryEntryRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.UNPROCESSED_DICTIONARY_ENTRY;

@Repository
public class UnprocessedDictionaryEntryDaoImpl extends AbstractDao<UnprocessedDictionaryEntry,
        Long, UnprocessedDictionaryEntryRecord> implements UnprocessedDictionaryEntryDao {

    private final DSLContext dslContext;

    @Autowired
    public UnprocessedDictionaryEntryDaoImpl(DSLContext dslContext) {

        super(dslContext, UNPROCESSED_DICTIONARY_ENTRY, UNPROCESSED_DICTIONARY_ENTRY.ID, UnprocessedDictionaryEntry.class);
        this.dslContext = dslContext;
    }

    @Override
    public List<UnprocessedDictionaryEntry> listDictionaryEntriesByDictionary(Long id) {
        final SelectQuery<Record> query = dslContext.select()
                .from(UNPROCESSED_DICTIONARY_ENTRY).getQuery();

        query.addConditions(UNPROCESSED_DICTIONARY_ENTRY.DICTIONARY_ID.eq(id));
        return query.fetchInto(UnprocessedDictionaryEntry.class);
    }

    @Override
    public void deleteDictionaryEntry(UnprocessedDictionaryEntry dictionaryEntry) {
        delete(dictionaryEntry);
        return;
    }
}
