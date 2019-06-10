package edu.mit.cci.pogs.view.study.beans;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;

public class SessionBean {

    private Session baseSession;

    private List<Session> childSessions;


    public SessionBean(Session s){
        this.baseSession = s;
        this.childSessions = new ArrayList<>();
    }

    public Session getBaseSession() {
        return baseSession;
    }

    public void setBaseSession(Session baseSession) {
        this.baseSession = baseSession;
    }

    public List<Session> getChildSessions() {
        return childSessions;
    }

    public void setChildSessions(List<Session> childSessions) {
        this.childSessions = childSessions;
    }
}
