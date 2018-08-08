package edu.mit.cci.pogs.view.session.beans;

import java.util.List;

public class SubjectCommunicationBean {

    private Long sessionId;

    private Long studyId;

    private List<String> selectedCommunications;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public List<String> getSelectedCommunications() {
        return selectedCommunications;
    }

    public void setSelectedCommunications(List<String> selectedCommunications) {
        this.selectedCommunications = selectedCommunications;
    }
}
