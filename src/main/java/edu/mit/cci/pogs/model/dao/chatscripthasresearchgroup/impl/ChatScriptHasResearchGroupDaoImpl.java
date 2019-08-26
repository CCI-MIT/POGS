package edu.mit.cci.pogs.model.dao.chatscripthasresearchgroup.impl;


import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.chatscripthasresearchgroup.ChatScriptHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatScriptHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.records.ChatScriptHasResearchGroupRecord;
import org.springframework.stereotype.Repository;

import static edu.mit.cci.pogs.model.jooq.tables.ChatScriptHasResearchGroup.CHAT_SCRIPT_HAS_RESEARCH_GROUP;

@Repository
public class ChatScriptHasResearchGroupDaoImpl  extends AbstractDao<ChatScriptHasResearchGroup, Long, ChatScriptHasResearchGroupRecord> implements ChatScriptHasResearchGroupDao {

    private final DSLContext dslContext;

    @Autowired
    public ChatScriptHasResearchGroupDaoImpl(DSLContext dslContext) {
        super(dslContext, CHAT_SCRIPT_HAS_RESEARCH_GROUP, CHAT_SCRIPT_HAS_RESEARCH_GROUP.ID, ChatScriptHasResearchGroup.class);
        this.dslContext = dslContext;
    }

    @Override
    public List<ChatScriptHasResearchGroup> list() {
        final SelectQuery<Record> query = dslContext.select()
                .from(CHAT_SCRIPT_HAS_RESEARCH_GROUP).getQuery();

        return query.fetchInto(ChatScriptHasResearchGroup.class);
    }

    @Override
    public List<ChatScriptHasResearchGroup> listByChatId(Long chatScriptId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(CHAT_SCRIPT_HAS_RESEARCH_GROUP).getQuery();
        query.addConditions(CHAT_SCRIPT_HAS_RESEARCH_GROUP.CHAT_SCRIPT_ID.eq(chatScriptId));
        return query.fetchInto(ChatScriptHasResearchGroup.class);
    }

    @Override
    public List<ChatScriptHasResearchGroup> listByResearchGroup(Long researchGroupId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(CHAT_SCRIPT_HAS_RESEARCH_GROUP).getQuery();
        query.addConditions(CHAT_SCRIPT_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID.eq(researchGroupId));
        return query.fetchInto(ChatScriptHasResearchGroup.class);
    }

    @Override
    public void delete(ChatScriptHasResearchGroup rghau){
        dslContext.delete(CHAT_SCRIPT_HAS_RESEARCH_GROUP)
                .where(CHAT_SCRIPT_HAS_RESEARCH_GROUP.ID.eq(rghau.getId()))
                .execute();
    }
}
