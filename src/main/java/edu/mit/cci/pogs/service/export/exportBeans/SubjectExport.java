package edu.mit.cci.pogs.service.export.exportBeans;

import java.sql.Timestamp;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;

public class SubjectExport extends Subject {


    public SubjectExport(){
        super();
    }
    public SubjectExport(Subject s){
        super(s);
    }
    private String subjectTeam;
    private Long subjectTeamId;

    public String getSubjectTeam() {
        return subjectTeam;
    }

    public void setSubjectTeam(String subjectTeam) {
        this.subjectTeam = subjectTeam;
    }

    public Long getSubjectTeamId() {
        return subjectTeamId;
    }

    public void setSubjectTeamId(Long subjectTeamId) {
        this.subjectTeamId = subjectTeamId;
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public String getSubjectExternalId() {
        return super.getSubjectExternalId();
    }

    @Override
    public String getSubjectDisplayName() {
        return super.getSubjectDisplayName();
    }

    @Override
    public Long getSessionId() {
        return super.getSessionId();
    }

    @Override
    public Long getPreviousSessionSubject() {
        return super.getPreviousSessionSubject();
    }

    @Override
    public String getPogsUniqueHash() {
        return super.getPogsUniqueHash();
    }

    @Override
    public Timestamp getCreatedAt() {
        return super.getCreatedAt();
    }
}
