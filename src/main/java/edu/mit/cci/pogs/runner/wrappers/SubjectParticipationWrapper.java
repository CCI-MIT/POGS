package edu.mit.cci.pogs.runner.wrappers;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;

public class SubjectParticipationWrapper {

    private Subject subject;

    private Integer participation;

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Integer getParticipation() {
        return participation;
    }

    public void setParticipation(Integer participation) {
        this.participation = participation;
    }
}
