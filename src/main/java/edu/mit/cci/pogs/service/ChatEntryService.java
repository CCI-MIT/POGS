package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.chatentry.ChatEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatEntry;
import edu.mit.cci.pogs.view.chatscript.beans.ChatEntriesBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatEntryService {

    private ChatEntryDao chatEntryDao;

    @Autowired
    public ChatEntryService(ChatEntryDao chatEntryDao) {
        this.chatEntryDao = chatEntryDao;
    }

    public void updateChatEntryList(ChatEntriesBean chatEntriesBean) {
        List<ChatEntry> chatEntryList = chatEntriesBean.getChatEntryList();
        for (ChatEntry chatEntry : chatEntryList) {
            chatEntry.setChatScriptId(chatEntriesBean.getChatscriptId());
            if(chatEntry.getId()!=null){
                chatEntryDao.update(chatEntry);
            }else{
                chatEntryDao.create(chatEntry);
            }
        }
    }

    public List<ChatEntry> listChatentriesByChatscriptId(Long chatscriptId) {
        return chatEntryDao.listChatEntryByChatScript(chatscriptId);
    }
}
