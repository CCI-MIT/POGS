package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.chatentry.ChatEntryDao;
import edu.mit.cci.pogs.model.dao.chatscript.ChatScriptDao;
import edu.mit.cci.pogs.model.dao.chatscripthasresearchgroup.ChatScriptHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatScriptHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatScript;
import edu.mit.cci.pogs.view.chatscript.beans.ChatScriptBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatEntry;
import edu.mit.cci.pogs.view.chatscript.beans.ChatEntriesBean;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatScriptService {

    private ChatEntryDao chatEntryDao;
    private ChatScriptDao chatScriptDao;
    private final ChatScriptHasResearchGroupDao chatScriptHasResearchGroupDao;

    @Autowired
    public ChatScriptService(ChatEntryDao chatEntryDao, ChatScriptDao chatScriptDao, ChatScriptHasResearchGroupDao chatScriptHasResearchGroupDao) {
        this.chatEntryDao = chatEntryDao;
        this.chatScriptDao = chatScriptDao;
        this.chatScriptHasResearchGroupDao = chatScriptHasResearchGroupDao;
    }

    public List<ChatEntry> listChatEntriesByChatscriptId(Long chatscriptId) {
        return chatEntryDao.listChatEntryByChatScript(chatscriptId);
    }

    private List<ChatScriptHasResearchGroup> listChatScriptHasResearchGroupByChatScript(Long chatScriptId) {
        return chatScriptHasResearchGroupDao.listByChatId(chatScriptId);
    }

    public List<ChatScriptHasResearchGroup> listChatScriptHasResearchGroupByChatScriptId(Long chatScriptId) {
        return this.chatScriptHasResearchGroupDao.listByChatId(chatScriptId);
    }

    public void updateChatEntryList(ChatEntriesBean chatEntriesBean) {
        List<ChatEntry> chatEntryList = chatEntriesBean.getChatEntryList();
        List<ChatEntry> existingChatEntries = chatEntryDao.listChatEntryByChatScript(chatEntriesBean.getChatscriptId());

        for (ChatEntry chatEntry : chatEntryList) {
            chatEntry.setChatScriptId(chatEntriesBean.getChatscriptId());
            if(chatEntry.getId()!=null){
                chatEntryDao.update(chatEntry);
                existingChatEntries.remove(chatEntry);
            }else{
                chatEntryDao.create(chatEntry);
            }
        }

        for (ChatEntry chatEntry: existingChatEntries){
            chatEntryDao.deleteChatEntry(chatEntry);
        }
    }

    private void createOrUpdateUserGroups(ChatScriptBean chatScriptBean) {
        if (chatScriptBean.getResearchGroupRelationshipBean() == null && chatScriptBean.getResearchGroupRelationshipBean().getSelectedValues() == null) {
            return;
        }
        List<ChatScriptHasResearchGroup> toCreate = new ArrayList<>();
        List<ChatScriptHasResearchGroup> toDelete = new ArrayList<>();
        List<ChatScriptHasResearchGroup> currentlySelected = listChatScriptHasResearchGroupByChatScript(chatScriptBean.getId());

        for (ChatScriptHasResearchGroup rghau : currentlySelected) {
            boolean foundRGH = false;
            for (String researchGroupId : chatScriptBean.getResearchGroupRelationshipBean().getSelectedValues()) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    foundRGH = true;
                }
            }
            if (!foundRGH) {
                toDelete.add(rghau);
            }

        }


        for (String researchGroupId : chatScriptBean.getResearchGroupRelationshipBean().getSelectedValues()) {

            boolean selectedAlreadyIn = false;
            for (ChatScriptHasResearchGroup rghau : currentlySelected) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    selectedAlreadyIn = true;
                }
            }
            if (!selectedAlreadyIn) {
                ChatScriptHasResearchGroup rghau = new ChatScriptHasResearchGroup();
                rghau.setChatScriptId(chatScriptBean.getId());
                rghau.setResearchGroupId(new Long(researchGroupId));
                toCreate.add(rghau);
            }

        }
        for (ChatScriptHasResearchGroup toCre : toCreate) {
            chatScriptHasResearchGroupDao.create(toCre);
        }
        for (ChatScriptHasResearchGroup toDel : toDelete) {
            chatScriptHasResearchGroupDao.delete(toDel);
        }

    }

    public ChatScript createOrUpdate(ChatScriptBean chatScriptBean) {

        ChatScript chatScript = new ChatScript();
        chatScript.setId(chatScriptBean.getId());
        chatScript.setChatScriptName(chatScriptBean.getChatScriptName());

        if (chatScript.getId() == null) {
            chatScript = chatScriptDao.create(chatScript);
            chatScriptBean.setId(chatScript.getId());
            createOrUpdateUserGroups(chatScriptBean);
        } else {
            chatScriptDao.update(chatScript);
            createOrUpdateUserGroups(chatScriptBean);
        }
        return chatScript;

    }
}
