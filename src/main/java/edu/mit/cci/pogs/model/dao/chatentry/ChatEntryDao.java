package edu.mit.cci.pogs.model.dao.chatentry;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatEntry;

import java.util.List;

public interface ChatEntryDao extends Dao<ChatEntry, Long> {
    List<ChatEntry> list();

    List<ChatEntry> listChatEntryByChatScript(Long id);
}
