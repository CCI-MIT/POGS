package edu.mit.cci.pogs.model.dao.chatentry.Impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.chatentry.ChatEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatEntry;
import edu.mit.cci.pogs.model.jooq.tables.records.ChatEntryRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.CHAT_ENTRY;

@Repository
public class ChatEntryDaoImpl extends AbstractDao<ChatEntry, Long, ChatEntryRecord> implements ChatEntryDao {

    private DSLContext dslContext;

    @Autowired
    public ChatEntryDaoImpl(DSLContext dslContext) {
        super(dslContext, CHAT_ENTRY, CHAT_ENTRY.ID, ChatEntry.class);
        this.dslContext = dslContext;
    }

    @Override
    public List<ChatEntry> list(){
        final SelectQuery<Record> query = dslContext.select(CHAT_ENTRY.fields())
                .from(CHAT_ENTRY)
                .getQuery();

        return query.fetchInto(ChatEntry.class);
    }

    @Override
    public List<ChatEntry> listChatEntryByChatScript(Long id){
        final SelectQuery<Record> query = dslContext.select()
                .from(CHAT_ENTRY).getQuery();

        query.addConditions(CHAT_ENTRY.CHAT_SCRIPT_ID.eq(id));
        return query.fetchInto(ChatEntry.class);
    }

    @Override
    public void deleteChatEntry(ChatEntry chatEntry){
        delete(chatEntry);
        return;
    }

}
