package edu.mit.cci.pogs.runner;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptException;

import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionExecutionAttribute;
import edu.mit.cci.pogs.runner.wrappers.SessionWrapper;
import edu.mit.cci.pogs.service.SessionExecutionAttributeService;
import edu.mit.cci.pogs.service.StudyAttributeService;
import edu.mit.cci.pogs.service.SubjectService;
import edu.mit.cci.pogs.service.TaskService;
import edu.mit.cci.pogs.service.TeamService;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionRelatedScriptRunner extends AbstractJavascriptRunner implements Runnable {


    protected SessionWrapper session;

    protected Long executableScriptId;

    private static final Logger _log = LoggerFactory.getLogger(SessionRelatedScriptRunner.class);

    @Autowired
    private SessionExecutionAttributeService sessionExecutionAttributeService;

    @Autowired
    private StudyAttributeService studyAttributeService;

    @Autowired
    protected SubjectDao subjectDao;

    @Autowired
    protected TeamService teamService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected SubjectService subjectService;

    @Autowired
    protected ExecutableScriptDao executableScriptDao;

    @Override
    public void setupVariableBindings() {

        this.getEngine().put("sessionId", session.getId());

        JSONArray teamMates = teamService.getTeamatesJSONObject(
                teamService.getTeamSubjects(null,session.getId(),null,null));

        this.getEngine().put("teammates", teamMates.toString());

        JSONArray sessionAttr = sessionExecutionAttributeService.
                listSessionExecutionAttributesAsJsonArray(session.getId());
        this.getEngine().put("sessionExecutionAttributes", sessionAttr.toString());


        JSONArray studyAttr = studyAttributeService.listStudyAttributesArray(session.getStudyId());
        this.getEngine().put("studyAttributes", studyAttr.toString());

    }

    protected void retreiveSubjectAttributesToAdd() {
        String subjectAttributesToAddJson = (String) this.getEngine()
                .getBindings(ScriptContext.ENGINE_SCOPE).get("subjectAttributesToAdd");
        subjectService.createOrUpdateSubjectsAttributes(subjectAttributesToAddJson);
    }

    protected void retrieveSessionExecutionAttributesToAdd() {
        String attributesToAddJson = (String) this.getEngine().getBindings(
                ScriptContext.ENGINE_SCOPE).get("sessionExecutionAttributesToAdd");
        sessionExecutionAttributeService.createSessionExecutionAttributesFromJsonString(
                attributesToAddJson,session.getId());

        updateSessionReferencesBaseedOnSessionAttributes();
    }

    protected void retrieveStudyAttributesToAdd() {
        String attributesToAddJson = (String) this.getEngine().getBindings(
                ScriptContext.ENGINE_SCOPE).get("studyAttributesToAdd");
        studyAttributeService.createOrUpdateStudyAttributes(
                attributesToAddJson,session.getId(), session.getStudyId());

        updateSessionReferencesBaseedOnSessionAttributes();
    }

    private void updateSessionReferencesBaseedOnSessionAttributes(){
        List<SessionExecutionAttribute> list = sessionExecutionAttributeService.listAttributes(session.getId());
        for(SessionExecutionAttribute sea: list){
            if(sea.getAttributeName().equals("SESSION_DONE_REDIRECT_URL")){
                //update session values if any
                SessionRunner sr = SessionRunnerManager.getSessionRunner(session.getId());
                //override attribute.
                sr.getSession().setDoneRedirectUrl(sea.getStringValue());
            }
        }
    }


    public void setSession(SessionWrapper session) {
        this.session = session;
    }


    @Override
    public void retrieveScriptVariables() {

        retreiveSubjectAttributesToAdd();
        retrieveSessionExecutionAttributesToAdd();
        retrieveStudyAttributesToAdd();
    }

    @Override
    public void handleScriptFailure(ScriptException se) {
        _log.error("After work script execution error for : " + se.getMessage());
    }

    @Override
    public void run() {
        Long timeBeforeStarts = (0l);


        try {
            if (timeBeforeStarts > 0) {
                System.out.println(" Time until session script starts : " + timeBeforeStarts);
                Thread.sleep(timeBeforeStarts);
            }

            System.out.println(" Session script is starting for session id: " + session.getId());

            ExecutableScript es = executableScriptDao.get(this.executableScriptId);
            this.runScript(es.getScriptContent(), null);

        } catch (InterruptedException ie) {
            _log.info("Stopping session script for session: " + session.getId());
        }

    }

    public void setExecutableScriptId(Long executableScriptId) {
        this.executableScriptId = executableScriptId;
    }
}
