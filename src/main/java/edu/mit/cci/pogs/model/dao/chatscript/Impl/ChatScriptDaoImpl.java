package edu.mit.cci.pogs.model.dao.chatscript.Impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.chatscript.ChatScriptDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatScript;
import edu.mit.cci.pogs.model.jooq.tables.records.ChatScriptRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import static edu.mit.cci.pogs.model.jooq.Tables.CHAT_SCRIPT;
import static edu.mit.cci.pogs.model.jooq.tables.ChatScriptHasResearchGroup.CHAT_SCRIPT_HAS_RESEARCH_GROUP;
import static edu.mit.cci.pogs.model.jooq.tables.ResearchGroupHasAuthUser.RESEARCH_GROUP_HAS_AUTH_USER;

import java.util.List;

@Repository
public class ChatScriptDaoImpl extends AbstractDao<ChatScript, Long, ChatScriptRecord> implements ChatScriptDao {

    private DSLContext dslContext;

    @Autowired
    public ChatScriptDaoImpl(DSLContext dslContext) {
        super(dslContext, CHAT_SCRIPT, CHAT_SCRIPT.ID, ChatScript.class);
        this.dslContext = dslContext;
    }

    @Override
    public List<ChatScript> list() {
        final SelectQuery<Record> query = dslContext.select()
                .from(CHAT_SCRIPT).getQuery();

        return query.fetchInto(ChatScript.class);
    }

    @Override
    public List<ChatScript> listChatScriptWithUserGroup(Long userId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(CHAT_SCRIPT)
                .join(CHAT_SCRIPT_HAS_RESEARCH_GROUP).on(CHAT_SCRIPT_HAS_RESEARCH_GROUP.CHAT_SCRIPT_ID.eq(CHAT_SCRIPT.ID))
                .join(RESEARCH_GROUP_HAS_AUTH_USER).on(RESEARCH_GROUP_HAS_AUTH_USER.RESEARCH_GROUP_ID.eq(CHAT_SCRIPT_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID))
                .where(RESEARCH_GROUP_HAS_AUTH_USER.AUTH_USER_ID.eq(userId))
                .getQuery();

        return query.fetchInto(ChatScript.class);

    }
}
