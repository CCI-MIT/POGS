package edu.mit.cci.pogs.model.dao.dictionaryhasresearchgroup.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.dictionaryhasresearchgroup.DictionaryHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.records.DictionaryHasResearchGroupRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.DICTIONARY_HAS_RESEARCH_GROUP;

@Repository
public class DictionaryHasResearchGroupDaoImpl extends AbstractDao<DictionaryHasResearchGroup, Long, DictionaryHasResearchGroupRecord> implements DictionaryHasResearchGroupDao {
    private final DSLContext dslContext;

    @Autowired
    public DictionaryHasResearchGroupDaoImpl(DSLContext dslContext) {
        super(dslContext, DICTIONARY_HAS_RESEARCH_GROUP, DICTIONARY_HAS_RESEARCH_GROUP.ID, DictionaryHasResearchGroup.class);
        this.dslContext = dslContext;
    }

    public List<DictionaryHasResearchGroup> list(){

        final SelectQuery<Record> query = dslContext.select()
                .from(DICTIONARY_HAS_RESEARCH_GROUP).getQuery();

        return query.fetchInto(DictionaryHasResearchGroup.class);
    }

    public List<DictionaryHasResearchGroup> listByDictionaryId(Long dictionaryId){

        final SelectQuery<Record> query = dslContext.select()
                .from(DICTIONARY_HAS_RESEARCH_GROUP).getQuery();
        query.addConditions(DICTIONARY_HAS_RESEARCH_GROUP.DICTIONARY_ID.eq(dictionaryId));
        return query.fetchInto(DictionaryHasResearchGroup.class);
    }

    public List<DictionaryHasResearchGroup> listByResearchGroup(Long researchGroupId){

        final SelectQuery<Record> query = dslContext.select()
                .from(DICTIONARY_HAS_RESEARCH_GROUP).getQuery();
        query.addConditions(DICTIONARY_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID.eq(researchGroupId));
        return query.fetchInto(DictionaryHasResearchGroup.class);
    }

    public void delete(DictionaryHasResearchGroup rghau) {
        dslContext.delete(DICTIONARY_HAS_RESEARCH_GROUP)
                .where(DICTIONARY_HAS_RESEARCH_GROUP.ID.eq(rghau.getId()))
                .execute();

    }
}
