package edu.mit.cci.pogs.view.chatscript.beans;

public class ChatScriptBean {

    private Long id;

    private String chatScriptName;

    public ChatScriptBean() {}

    public ChatScriptBean(
            Long   id,
            String chatScriptName
    ) {
        this.id = id;
        this.chatScriptName = chatScriptName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChatScriptName() {
        return chatScriptName;
    }

    public void setChatScriptName(String chatScriptName) {
        this.chatScriptName = chatScriptName;
    }
}
