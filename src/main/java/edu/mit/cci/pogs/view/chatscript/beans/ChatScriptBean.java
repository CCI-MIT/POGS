package edu.mit.cci.pogs.view.chatscript.beans;

import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatScript;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;

public class ChatScriptBean extends ChatScript {


    public ResearchGroupRelationshipBean getResearchGroupRelationshipBean() {
        return researchGroupRelationshipBean;
    }

    public void setResearchGroupRelationshipBean(ResearchGroupRelationshipBean researchGroupRelationshipBean) {
        this.researchGroupRelationshipBean = researchGroupRelationshipBean;
    }

    private ResearchGroupRelationshipBean researchGroupRelationshipBean;

    public ChatScriptBean() {}

    public ChatScriptBean(ChatScript chatScript) {
        super(chatScript);
    }


}
