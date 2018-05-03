package edu.mit.cci.pogs.view.study.beans;


import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;

public class StudyBean {


    private Long   id;

    private String studyName;

    private String studyDescription;

    private String studySessionPrefix;

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
}
