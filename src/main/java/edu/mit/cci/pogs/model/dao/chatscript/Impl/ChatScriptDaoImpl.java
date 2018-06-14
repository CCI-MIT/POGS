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
}
