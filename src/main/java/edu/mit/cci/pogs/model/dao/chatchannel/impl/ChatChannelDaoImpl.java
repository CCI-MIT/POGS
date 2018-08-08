package edu.mit.cci.pogs.model.dao.chatchannel.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.chatchannel.ChatChannelDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatChannel;
import edu.mit.cci.pogs.model.jooq.tables.records.ChatChannelRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.CHAT_CHANNEL;

@Repository
public class ChatChannelDaoImpl extends AbstractDao<ChatChannel, Long, ChatChannelRecord> implements ChatChannelDao {

    private final DSLContext dslContext;

    @Autowired
    public ChatChannelDaoImpl(DSLContext dslContext) {
        super(dslContext, CHAT_CHANNEL, CHAT_CHANNEL.ID, ChatChannel.class);
        this.dslContext = dslContext;
    }

    public List<ChatChannel> list() {

        final SelectQuery<Record> query = dslContext.select()
                .from(CHAT_CHANNEL).getQuery();

        return query.fetchInto(ChatChannel.class);
    }

    public List<ChatChannel> listBySessionId(Long sessionId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(CHAT_CHANNEL).getQuery();

        query.addConditions(CHAT_CHANNEL.SESSION_ID.eq(sessionId));
        return query.fetchInto(ChatChannel.class);
    }

    public void delete(ChatChannel rghau) {
        dslContext.delete(CHAT_CHANNEL)
                .where(CHAT_CHANNEL.ID.eq(rghau.getId()))
                .execute();

    }

}
 
