package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.chatentry.ChatEntryDao;
import edu.mit.cci.pogs.model.dao.chatscript.ChatScriptDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatEntry;
import edu.mit.cci.pogs.view.chatscript.beans.ChatEntriesBean;

import java.util.List;

@Service
public class ChatScriptService {

    private ChatEntryDao chatEntryDao;
    private ChatScriptDao chatScriptDao;

    @Autowired
    public ChatScriptService(ChatEntryDao chatEntryDao, ChatScriptDao chatScriptDao) {
        this.chatEntryDao = chatEntryDao;
        this.chatScriptDao = chatScriptDao;
    }

    public List<ChatEntry> listChatEntriesByChatscriptId(Long chatscriptId) {
        return chatEntryDao.listChatEntryByChatScript(chatscriptId);
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
}
