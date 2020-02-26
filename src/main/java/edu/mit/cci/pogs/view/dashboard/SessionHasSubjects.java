package edu.mit.cci.pogs.view.dashboard;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasSessionCheckIn;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;

public class SessionHasSubjects {

    private SessionWrapper session;

    private List<SubjectHasSessionCheckInBean> subjects;


    public Session getSession() {
        return session;
    }

    public void setSession(SessionWrapper session) {
        this.session = session;
    }

    public List<SubjectHasSessionCheckInBean> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectHasSessionCheckInBean> subjects) {
        this.subjects = subjects;
    }
}
