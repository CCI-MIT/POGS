package edu.mit.cci.pogs.view.session.beans;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectCommunication;

public class SubjectBean extends Subject {


    private List<SubjectAttribute> subjectAttributes;

    public SubjectBean() {

    }

    public SubjectBean(Subject su) {
        super(su);
    }

    private List<SubjectCommunication> subjectCommunications;

    public List<SubjectCommunication> getSubjectCommunications() {
        return subjectCommunications;
    }

    public void setSubjectCommunications(List<SubjectCommunication> subjectCommunications) {
        this.subjectCommunications = subjectCommunications;
    }

    public List<SubjectAttribute> getSubjectAttributes() {
        return subjectAttributes;
    }

    public void setSubjectAttributes(List<SubjectAttribute> subjectAttributes) {
        this.subjectAttributes = subjectAttributes;
    }

    public boolean isNullEntry() {
        if (getId() == null &&
                getSessionId() == null &&
                getSubjectAttributes() == null &&
                getSubjectCommunications() == null &&
                getSubjectExternalId() == null &&
                getSubjectDisplayName() == null &&
                getPreviousSessionSubject() == null
        ) {
            return true;
        } else {
            return false;
        }

    }
}
