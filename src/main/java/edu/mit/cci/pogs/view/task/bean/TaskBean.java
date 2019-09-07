package edu.mit.cci.pogs.view.task.bean;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;

public class TaskBean extends Task{

    private Long taskConfigurationId;



    private ResearchGroupRelationshipBean researchGroupRelationshipBean;

    public TaskBean() {
    }

    public TaskBean(Task value) {
        super(value);

    }

    public ResearchGroupRelationshipBean getResearchGroupRelationshipBean() {
        return researchGroupRelationshipBean;
    }

    public void setResearchGroupRelationshipBean(ResearchGroupRelationshipBean researchGroupRelationshipBean) {
        this.researchGroupRelationshipBean = researchGroupRelationshipBean;
    }


    public Long getTaskConfigurationId() {
        return taskConfigurationId;
    }

    public void setTaskConfigurationId(Long taskConfigurationId) {
        this.taskConfigurationId = taskConfigurationId;
    }

}
