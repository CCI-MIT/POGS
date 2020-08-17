package edu.mit.cci.pogs.runner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.service.EventLogService;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionReplayScriptProcessor {

    private Long sessionToReplayFrom;

    private Long sessionScriptToReplayFrom;

    @Autowired
    private EventLogService eventLogService;

    @Autowired
    private ExecutableScriptDao executableScriptDao;

    public JSONObject processAndGenerateScriptEntries(){
        //setup the variables/script from event log
        String code = null;
        if(sessionScriptToReplayFrom==null) {
            code = eventLogService.getScriptForLogs(sessionToReplayFrom);
        } else {
            ExecutableScript ex = executableScriptDao.get(sessionScriptToReplayFrom);
            if(ex!=null){
                code = ex.getScriptContent();
            }
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine;

        engine = manager.getEngineByName("JavaScript");

        try {
            Reader scriptReader = new InputStreamReader(new ByteArrayInputStream(code.getBytes()));

            engine.eval(scriptReader);
            scriptReader.close();

        } catch (ScriptException |IOException se) {
            //plugin script failed.
            System.out.println(se);
            se.printStackTrace();
        }

        String sessionEvents = (String) engine
                .getBindings(ScriptContext.ENGINE_SCOPE).get("sessionEvents");
        System.out.println(sessionEvents);
        JSONObject sessEv = new JSONObject(sessionEvents);

        return sessEv;
    }

    public Long getSessionToReplayFrom() {
        return sessionToReplayFrom;
    }

    public void setSessionToReplayFrom(Long sessionToReplayFrom) {
        this.sessionToReplayFrom = sessionToReplayFrom;
    }

    public Long getSessionScriptToReplayFrom() {
        return sessionScriptToReplayFrom;
    }

    public void setSessionScriptToReplayFrom(Long sessionScriptToReplayFrom) {
        this.sessionScriptToReplayFrom = sessionScriptToReplayFrom;
    }
}
