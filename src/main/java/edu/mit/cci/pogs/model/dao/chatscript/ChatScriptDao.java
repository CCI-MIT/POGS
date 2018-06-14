package edu.mit.cci.pogs.model.dao.chatscript;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatScript;

import java.util.List;

public interface ChatScriptDao extends Dao<ChatScript, Long> {
    List<ChatScript> list();
}
