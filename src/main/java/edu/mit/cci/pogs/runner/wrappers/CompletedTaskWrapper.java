package edu.mit.cci.pogs.runner.wrappers;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;

public class CompletedTaskWrapper extends CompletedTask {

    private List<SubjectParticipationWrapper> subjectParticipations;

    public CompletedTaskWrapper(CompletedTask value) {
        super(value);
    }


    public List<SubjectParticipationWrapper> getSubjectParticipations() {
        return subjectParticipations;
    }

    public void setSubjectParticipations(List<SubjectParticipationWrapper> subjectParticipations) {
        this.subjectParticipations = subjectParticipations;
    }
}
