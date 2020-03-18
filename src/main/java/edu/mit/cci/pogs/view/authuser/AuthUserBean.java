package edu.mit.cci.pogs.view.authuser;

import edu.mit.cci.pogs.model.jooq.tables.pojos.AuthUser;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;

public class AuthUserBean {

    private Long id;
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String password;
    private Boolean isAdmin;


    private ResearchGroupRelationshipBean researchGroupRelationshipBean;

    public AuthUserBean() {

    }

    public AuthUserBean(AuthUser pojo) {
        this.id = pojo.getId();
        this.emailAddress = pojo.getEmailAddress();
        this.firstName = pojo.getFirstName();
        this.lastName = pojo.getLastName();
        this.password = pojo.getPassword();
        this.isAdmin = true;
    }

    public AuthUser getAuthUser() {
        AuthUser ret = new AuthUser();
        ret.setId(id);
        ret.setEmailAddress(emailAddress);
        ret.setFirstName(firstName);
        ret.setLastName(lastName);
        ret.setPassword(password);
        ret.setIsAdmin(getAdmin());
        return ret;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public ResearchGroupRelationshipBean getResearchGroupRelationshipBean() {
        return researchGroupRelationshipBean;
    }

    public void setResearchGroupRelationshipBean(ResearchGroupRelationshipBean researchGroupRelationshipBean) {
        this.researchGroupRelationshipBean = researchGroupRelationshipBean;
    }
}
