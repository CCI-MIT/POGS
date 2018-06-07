package edu.mit.cci.pogs.view.chatscript.beans;

import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatEntry;

import java.util.List;

public class ChatEntriesBean {

    List<ChatEntry> chatEntryList;

    private Long chatscriptId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;

    public List<ChatEntry> getChatEntryList() {
        return chatEntryList;
    }

    public void setChatEntryList(List<ChatEntry> chatEntryList) {
        this.chatEntryList = chatEntryList;
    }

    public Long getChatscriptId() {
        return chatscriptId;
    }

    public void setChatscriptId(Long chatscriptId) {
        this.chatscriptId = chatscriptId;
    }
}
