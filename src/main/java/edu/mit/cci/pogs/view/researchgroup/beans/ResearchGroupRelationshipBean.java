package edu.mit.cci.pogs.view.researchgroup.beans;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroupHasAuthUser;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroupHasResearchGroup;
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

    public void setTaskyHasResearchSelectedValues(List<TaskHasResearchGroup> taskHasResearchGroup){
        if(taskHasResearchGroup!=null && !taskHasResearchGroup.isEmpty()){
            List<String> selectedValues = new ArrayList<>();
            for(TaskHasResearchGroup rghau: taskHasResearchGroup){
                selectedValues.add(rghau.getResearchGroupId().toString());
            }
            this.selectedValues = selectedValues.toArray( new String[0]);
        }
    }

    public void setTaskGroupHasResearchSelectedValues(List<TaskGroupHasResearchGroup> taskGroupHasResearchGroup){
        if(taskGroupHasResearchGroup!=null && !taskGroupHasResearchGroup.isEmpty()){
            List<String> selectedValues = new ArrayList<>();
            for(TaskGroupHasResearchGroup rghau: taskGroupHasResearchGroup){
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
