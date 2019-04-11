package edu.mit.cci.pogs.model.dao.dictionary.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.dictionary.DictionaryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;
import edu.mit.cci.pogs.model.jooq.tables.records.DictionaryRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.DICTIONARY;

@Repository
public class DictionaryDaoImpl extends AbstractDao<Dictionary, Long, DictionaryRecord> implements DictionaryDao {

    private final DSLContext dslContext;

    @Autowired
    public DictionaryDaoImpl(DSLContext dslContext) {
        super(dslContext, DICTIONARY, DICTIONARY.ID, Dictionary.class);
        this.dslContext = dslContext;
    }

    @Override
    public List<Dictionary> list() {
        final SelectQuery<Record> query = dslContext.select()
                .from(DICTIONARY).getQuery();

        return query.fetchInto(Dictionary.class);
    }
}
