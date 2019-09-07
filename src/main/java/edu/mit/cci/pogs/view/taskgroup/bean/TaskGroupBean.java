package edu.mit.cci.pogs.view.taskgroup.bean;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskGroup;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;

public class TaskGroupBean extends TaskGroup{


    private List<Long> selectedTasks;

    private ResearchGroupRelationshipBean researchGroupRelationshipBean;

    public TaskGroupBean() {

    }

    public TaskGroupBean(TaskGroup tg) {
        super(tg);
    }


    public List<Long> getSelectedTasks() {
        return selectedTasks;
    }

    public void setSelectedTasks(List<Long> selectedTasks) {
        this.selectedTasks = selectedTasks;
    }

    public ResearchGroupRelationshipBean getResearchGroupRelationshipBean() {
        return researchGroupRelationshipBean;
    }

    public void setResearchGroupRelationshipBean(ResearchGroupRelationshipBean researchGroupRelationshipBean) {
        this.researchGroupRelationshipBean = researchGroupRelationshipBean;
    }

}
