package edu.mit.cci.pogs.view.authuser.beans;

import edu.mit.cci.pogs.model.jooq.tables.pojos.AuthUser;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;

public class AuthUserBean extends AuthUser{

    private ResearchGroupRelationshipBean researchGroupRelationshipBean;

    public AuthUserBean() {

    }

    public AuthUserBean(AuthUser pojo) {
        super(pojo);
    }

    public ResearchGroupRelationshipBean getResearchGroupRelationshipBean() {
        return researchGroupRelationshipBean;
    }

    public void setResearchGroupRelationshipBean(ResearchGroupRelationshipBean researchGroupRelationshipBean) {
        this.researchGroupRelationshipBean = researchGroupRelationshipBean;
    }
}
