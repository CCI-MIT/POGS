package edu.mit.cci.pogs.view.study.beans;


import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;

public class StudyBean {

    public StudyBean() {

    }

    public StudyBean(Study study) {

        this.id = study.getId();
        this.studyName = study.getStudyName();
        this.studyDescription = study.getStudyDescription();
        this.studySessionPrefix = study.getStudySessionPrefix();

    }


    private Long id;

    private String studyName;

    private String studyDescription;

    private String studySessionPrefix;

    private ResearchGroupRelationshipBean researchGroupRelationshipBean;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getStudyDescription() {
        return studyDescription;
    }

    public void setStudyDescription(String studyDescription) {
        this.studyDescription = studyDescription;
    }

    public String getStudySessionPrefix() {
        return studySessionPrefix;
    }

    public void setStudySessionPrefix(String studySessionPrefix) {
        this.studySessionPrefix = studySessionPrefix;
    }

    public ResearchGroupRelationshipBean getResearchGroupRelationshipBean() {
        return researchGroupRelationshipBean;
    }

    public void setResearchGroupRelationshipBean(ResearchGroupRelationshipBean researchGroupRelationshipBean) {
        this.researchGroupRelationshipBean = researchGroupRelationshipBean;
    }
}
