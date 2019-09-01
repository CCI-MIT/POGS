package edu.mit.cci.pogs.view.researchgroup.beans;

import java.lang.reflect.Method;
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


    public <T> void setObjectHasResearchSelectedValues(List<T> objectHasResearchGroup){
        if(objectHasResearchGroup!=null && !objectHasResearchGroup.isEmpty()){
            List<String> selectedValues = new ArrayList<>();
            for(T rghau: objectHasResearchGroup){
                for (Method declaredMethod : rghau.getClass().getDeclaredMethods()) {
                    declaredMethod.getName().equalsIgnoreCase("getResearchGroupId");
                    try {
                        String researchGroupId = declaredMethod.invoke(rghau).toString();
                        selectedValues.add(researchGroupId);
                    }
                    catch (Exception e){
                        System.out.println("Incorrect paremter type for Research Group selection");
                    }
                }

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
