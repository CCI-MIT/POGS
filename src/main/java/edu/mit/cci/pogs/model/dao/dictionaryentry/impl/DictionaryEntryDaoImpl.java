package edu.mit.cci.pogs.model.dao.dictionaryentry.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;
import edu.mit.cci.pogs.model.jooq.tables.records.DictionaryEntryRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.DICTIONARY_ENTRY;

@Repository
public class DictionaryEntryDaoImpl extends AbstractDao<DictionaryEntry, Long, DictionaryEntryRecord> implements DictionaryEntryDao {

    private final DSLContext dslContext;

    @Autowired
    public DictionaryEntryDaoImpl(DSLContext dslContext) {
        super(dslContext, DICTIONARY_ENTRY, DICTIONARY_ENTRY.ID, DictionaryEntry.class);
        this.dslContext = dslContext;
    }

    @Override
    public List<DictionaryEntry> listDictionaryEntriesByDictionary(Long id){
        final SelectQuery<Record> query = dslContext.select()
                .from(DICTIONARY_ENTRY).getQuery();

        query.addConditions(DICTIONARY_ENTRY.DICTIONARY_ID.eq(id));
        return query.fetchInto(DictionaryEntry.class);
    }

    @Override
    public void deleteDictionaryEntry(DictionaryEntry dictionaryEntry){
        delete(dictionaryEntry);
        return;
    }
}
