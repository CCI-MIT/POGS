package edu.mit.cci.pogs.model.dao.chatscripthasresearchgroup;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatScriptHasResearchGroup;

import java.util.List;

public interface ChatScriptHasResearchGroupDao extends Dao<ChatScriptHasResearchGroup, Long> {
    List<ChatScriptHasResearchGroup> list();
    List<ChatScriptHasResearchGroup> listByChatId(Long chatScriptId);
    List<ChatScriptHasResearchGroup> listByResearchGroup(Long researchGroupId);
    void delete(ChatScriptHasResearchGroup rghau);
}
