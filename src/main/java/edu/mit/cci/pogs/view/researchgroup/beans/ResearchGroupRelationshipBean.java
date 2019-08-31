package edu.mit.cci.pogs.view.researchgroup.beans;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.*;


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

    public void setChatScriptHasResearchSelectedValues(List<ChatScriptHasResearchGroup> dictionaryHasResearchGroup){
        if(dictionaryHasResearchGroup!=null && !dictionaryHasResearchGroup.isEmpty()){
            List<String> selectedValues = new ArrayList<>();
            for(ChatScriptHasResearchGroup rghau: dictionaryHasResearchGroup){
                selectedValues.add(rghau.getResearchGroupId().toString());
            }
            this.selectedValues = selectedValues.toArray( new String[0]);
        }
    }
    public void setDictionaryHasResearchSelectedValues(List<DictionaryHasResearchGroup> dictionaryHasResearchGroup){
        if(dictionaryHasResearchGroup!=null && !dictionaryHasResearchGroup.isEmpty()){
            List<String> selectedValues = new ArrayList<>();
            for(DictionaryHasResearchGroup rghau: dictionaryHasResearchGroup){
                selectedValues.add(rghau.getResearchGroupId().toString());
            }
            this.selectedValues = selectedValues.toArray( new String[0]);
        }
    }
    public void setTaskConfigurationHasResearchSelectedValues(List<TaskConfigurationHasResearchGroup> taskConfigurationHasResearchGroup){
        if(taskConfigurationHasResearchGroup!=null && !taskConfigurationHasResearchGroup.isEmpty()){
            List<String> selectedValues = new ArrayList<>();
            for(TaskConfigurationHasResearchGroup rghau: taskConfigurationHasResearchGroup){
                selectedValues.add(rghau.getResearchGroupId().toString());
            }
            this.selectedValues = selectedValues.toArray( new String[0]);
        }
    }

    public void setExecutableScriptHasResearchSelectedValues(List<ExecutableScriptHasResearchGroup> executableScriptHasResearchGroup){
        if(executableScriptHasResearchGroup!=null && !executableScriptHasResearchGroup.isEmpty()){
            List<String> selectedValues = new ArrayList<>();
            for(ExecutableScriptHasResearchGroup rghau: executableScriptHasResearchGroup){
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
