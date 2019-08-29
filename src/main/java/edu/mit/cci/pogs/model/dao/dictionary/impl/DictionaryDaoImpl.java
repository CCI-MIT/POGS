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

import static edu.mit.cci.pogs.model.jooq.Tables.*;

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

    public List<Dictionary> listDictionariesWithUserGroup(Long userId){

        final SelectQuery<Record> query = dslContext.select(DICTIONARY.fields())
                .from(DICTIONARY)
                .join(DICTIONARY_HAS_RESEARCH_GROUP).on(DICTIONARY_HAS_RESEARCH_GROUP.DICTIONARY_ID.eq(DICTIONARY.ID))
                .join(RESEARCH_GROUP_HAS_AUTH_USER).on(RESEARCH_GROUP_HAS_AUTH_USER.RESEARCH_GROUP_ID.eq(DICTIONARY_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID))
                .where(RESEARCH_GROUP_HAS_AUTH_USER.AUTH_USER_ID.eq(userId))
                .orderBy(DICTIONARY.ID)
                .getQuery();

        return query.fetchInto(Dictionary.class);
    }
}
