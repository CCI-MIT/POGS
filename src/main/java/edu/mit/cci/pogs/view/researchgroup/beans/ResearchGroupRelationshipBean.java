package edu.mit.cci.pogs.view.researchgroup.beans;

import java.util.ArrayList;
import java.util.List;


import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroupHasAuthUser;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasResearchGroup;


public class ResearchGroupRelationshipBean {

    public ResearchGroupRelationshipBean(){

    }
    public void setResearchGroupHasAuthUsersSelectedValues(List<ResearchGroupHasAuthUser> researchGroupHasAuthUsers){
        if(researchGroupHasAuthUsers!=null && !researchGroupHasAuthUsers.isEmpty()){
            List<String> selectedValues = new ArrayList<>();
            for(ResearchGroupHasAuthUser rghau: researchGroupHasAuthUsers){
                selectedValues.add(rghau.getResearchGroupId().toString());
            }
            this.selectedValues = selectedValues.toArray( new String[0]);
        }
    }
    public void setStudyHasResearchSelectedValues(List<StudyHasResearchGroup> studyHasResearchGroup){
        if(studyHasResearchGroup!=null && !studyHasResearchGroup.isEmpty()){
            List<String> selectedValues = new ArrayList<>();
            for(StudyHasResearchGroup rghau: studyHasResearchGroup){
                selectedValues.add(rghau.getResearchGroupId().toString());
            }
            this.selectedValues = selectedValues.toArray( new String[0]);
        }
    }

    public void setDictionaryHasResearchSelectedValues(List<DictionaryHasResearchGroup> dictionaryHasResearchGroup){
        if(dictionaryHasResearchGroup!=null && !dictionaryHasResearchGroup.isEmpty()){
            List<String> selectedValues = new ArrayList<>();
            for(DictionaryHasResearchGroup rghau: dictionaryHasResearchGroup){
                selectedValues.add(rghau.getDictionaryId().toString());
            }
            this.selectedValues = selectedValues.toArray( new String[0]);
        }
    }

    public void setTaskyHasResearchSelectedValues(List<TaskHasResearchGroup> studyHasResearchGroup){
        if(studyHasResearchGroup!=null && !studyHasResearchGroup.isEmpty()){
            List<String> selectedValues = new ArrayList<>();
            for(TaskHasResearchGroup rghau: studyHasResearchGroup){
                selectedValues.add(rghau.getResearchGroupId().toString());
            }
            this.selectedValues = selectedValues.toArray( new String[0]);
        }
    }

    private String[] selectedValues;

    public String[] getSelectedValues() {
        return selectedValues;
    }

    public void setSelectedValues(String[] selectedValues) {
        this.selectedValues = selectedValues;
    }
}
