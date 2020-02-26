package edu.mit.cci.pogs.view.dashboard;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasSessionCheckIn;

public class SubjectHasSessionCheckInBean extends SubjectHasSessionCheckIn {

    private Subject subject;

    public SubjectHasSessionCheckInBean(SubjectHasSessionCheckIn con){
        super(con);
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
