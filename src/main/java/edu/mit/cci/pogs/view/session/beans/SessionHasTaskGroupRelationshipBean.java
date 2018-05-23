package edu.mit.cci.pogs.view.session.beans;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;

public class SessionHasTaskGroupRelationshipBean {


    public void setSessionHasTaskGroupSelectedValues(List<SessionHasTaskGroup> sessionHasTaskGroup) {
        if (sessionHasTaskGroup != null && !sessionHasTaskGroup.isEmpty()) {
            List<String> selectedValues = new ArrayList<>();
            for (SessionHasTaskGroup rghau : sessionHasTaskGroup) {
                selectedValues.add(rghau.getTaskGroupId().toString());
            }
            this.selectedValues = selectedValues.toArray(new String[0]);
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
