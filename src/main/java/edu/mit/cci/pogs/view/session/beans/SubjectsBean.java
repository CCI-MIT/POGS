package edu.mit.cci.pogs.view.session.beans;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;

public class SubjectsBean {

    private List<Subject> subjectList;

    private Long sessionId;

    private Long studyId;

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<Subject> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }
}
