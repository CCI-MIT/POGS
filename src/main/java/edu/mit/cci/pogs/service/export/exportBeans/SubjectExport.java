package edu.mit.cci.pogs.service.export.exportBeans;

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
}
