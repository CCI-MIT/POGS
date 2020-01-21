package edu.mit.cci.pogs.runner;

import org.json.JSONArray;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.script.ScriptContext;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PreviewTaskBeforeWorkRunner extends TaskBeforeWorkRunner {

    private org.json.JSONArray executionAttributes;
    private Subject fakeSubject;
    private org.json.JSONArray team;

    private String subjectAttributesToAdd;
    private String completedTaskAttributesToAdd;


    @Override
    public void setupVariableBindings() {
        //get all subjects + subjects attributes

        boolean isSoloTask = false;
        this.getEngine().put("sessionId", -1);
        this.getEngine().put("completedTaskId", -1);

        if(this.getCompletedTask().getSubjectId()!=null){
            isSoloTask = true;

            this.getEngine().put("subject", fakeSubject);
        } else {
            this.getEngine().put("subject", null);
        }

        this.getEngine().put("isSoloTask", isSoloTask);


        this.getEngine().put("teammates", team);



        //get all task_execution_attribute
        this.getEngine().put("taskConfigurationAttributes", executionAttributes);

        this.getEngine().put("completedTaskAttributes", "[{" +
                "    \"attributeName\": \"padID\"," +
                "    \"stringValue\": \"TESTPAD\"" +
                "}]");

        System.out.println("taskConfigurationAttributes : " + executionAttributes);



    }

    @Override
    public void retrieveScriptVariables() {

        retreiveSubjectAttributesToAdd();
        retrieveCompletedTaskAttributesToAdd();

    }

    protected void retreiveSubjectAttributesToAdd() {
         subjectAttributesToAdd = (String) this.getEngine()
                .getBindings(ScriptContext.ENGINE_SCOPE).get("subjectAttributesToAdd");
    }

    protected void retrieveCompletedTaskAttributesToAdd() {
         completedTaskAttributesToAdd = (String) this.getEngine().getBindings(
                ScriptContext.ENGINE_SCOPE).get("completedTaskAttributesToAdd");

    }

    public String getSubjectAttributesToAdd() {
        return subjectAttributesToAdd;
    }

    public void setSubjectAttributesToAdd(String subjectAttributesToAdd) {
        this.subjectAttributesToAdd = subjectAttributesToAdd;
    }

    public String getCompletedTaskAttributesToAdd() {
        return completedTaskAttributesToAdd;
    }

    public void setCompletedTaskAttributesToAdd(String completedTaskAttributesToAdd) {
        this.completedTaskAttributesToAdd = completedTaskAttributesToAdd;
    }

    public JSONArray getExecutionAttributes() {
        return executionAttributes;
    }

    public void setExecutionAttributes(JSONArray executionAttributes) {
        this.executionAttributes = executionAttributes;
    }

    public Subject getFakeSubject() {
        return fakeSubject;
    }

    public void setFakeSubject(Subject fakeSubject) {
        this.fakeSubject = fakeSubject;
    }

    public JSONArray getTeam() {
        return team;
    }

    public void setTeam(JSONArray team) {
        this.team = team;
    }
}
