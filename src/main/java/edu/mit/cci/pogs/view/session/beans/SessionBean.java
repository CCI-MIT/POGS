package edu.mit.cci.pogs.view.session.beans;

import java.sql.Timestamp;

import edu.mit.cci.pogs.model.dao.session.CommunicationConstraint;
import edu.mit.cci.pogs.model.dao.session.SessionStatus;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;

public class SessionBean extends Session{

    private SessionHasTaskGroupRelationshipBean sessionHasTaskGroupRelationshipBean;

    public SessionBean() {

    }

    public SessionBean(Session value) {
        super(value);
    }

    public SessionHasTaskGroupRelationshipBean getSessionHasTaskGroupRelationshipBean() {
        return sessionHasTaskGroupRelationshipBean;
    }

    public void setSessionHasTaskGroupRelationshipBean(SessionHasTaskGroupRelationshipBean sessionHasTaskGroupRelationshipBean) {
        this.sessionHasTaskGroupRelationshipBean = sessionHasTaskGroupRelationshipBean;
    }

    public boolean getHasSessionStarted() {
        if(getStatus()!=null) {
            return !getStatus()
                    .equals(SessionStatus.NOTSTARTED.getId().toString());
        }
        return false;

    }
}
