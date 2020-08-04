package edu.mit.cci.pogs.service.export.exportBeans;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.utils.ExportUtils;

public class SubjectStudyScore {

    private Subject subject;

    private List<SubjectAttribute> subjectAttribute;

    private List<SessionRelatedScore> relatedScoreList;

    public String getWorkerId(){
        for (SubjectAttribute sa:this.subjectAttribute) {
            if (sa != null && sa.getAttributeName().equals("workerId")) {
                if (sa.getStringValue() != null) {

                   return  sa.getStringValue();
                }
            }
        }

            return "N/A";

    }

    public String getExtraColumns(){
        String ret = "";
        List<String> extraColumns = new ArrayList<>();

        if(this.getRelatedScoreList() != null &&
                this.getRelatedScoreList().size() > 0){

            for(int i =0; i < this.getRelatedScoreList().size(); i++){
                SessionRelatedScore ssz = this.getRelatedScoreList().get(i);

                extraColumns.add(ssz.getSession().getId() + "");
                extraColumns.add(ssz.getSession().getFullSessionName());
                extraColumns.add(ExportUtils.getTimeFormatted(ssz.getSession().getSessionStartDate()));

                for(int j=0; j < ssz.getTaskNames().size(); j ++){

                    Double ind =0.0 ;
                    if(ssz.getTaskIndividualScores()!=null && ssz.getTaskIndividualScores().size()> j){
                        ind = ssz.getTaskIndividualScores().get(j);
                    }
                    extraColumns.add(ind + "");
                    Double group = 0.0;
                    if(ssz.getTaskGroupScores()!=null && ssz.getTaskGroupScores().size()> j){
                        group = ssz.getTaskGroupScores().get(j);
                    }
                    extraColumns.add(group + "");
                }
            }
        }
        for(int i =0; i < extraColumns.size(); i++){
            ret += extraColumns.get(i) +";";
        }
        return ret;
    }
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<SessionRelatedScore> getRelatedScoreList() {
        return relatedScoreList;
    }

    public void setRelatedScoreList(List<SessionRelatedScore> relatedScoreList) {
        this.relatedScoreList = relatedScoreList;
    }

    public List<SubjectAttribute> getSubjectAttribute() {
        return subjectAttribute;
    }

    public void setSubjectAttribute(List<SubjectAttribute> subjectAttribute) {
        this.subjectAttribute = subjectAttribute;
    }
}
