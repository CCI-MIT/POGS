package edu.mit.cci.pogs.view.executablescript.beans;

import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;

public class ExecutableScriptBean extends ExecutableScript{

    private ResearchGroupRelationshipBean researchGroupRelationshipBean;

    public ExecutableScriptBean(){

    }

    public ExecutableScriptBean(ExecutableScript executableScript){

        super(executableScript);

    }

    public ResearchGroupRelationshipBean getResearchGroupRelationshipBean() {
        return researchGroupRelationshipBean;
    }

    public void setResearchGroupRelationshipBean(ResearchGroupRelationshipBean researchGroupRelationshipBean) {
        this.researchGroupRelationshipBean = researchGroupRelationshipBean;
    }

}
