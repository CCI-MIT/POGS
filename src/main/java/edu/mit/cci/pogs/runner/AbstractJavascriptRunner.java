package edu.mit.cci.pogs.runner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public abstract class AbstractJavascriptRunner {

    private ScriptEngine engine;

    private Long externalReferenceId;

    public void runScript(String code, Long externalReferenceId) {

        this.externalReferenceId = externalReferenceId;

        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");

        setupVariableBindings();

        Reader scriptReader = new InputStreamReader(new ByteArrayInputStream(code.getBytes()));
        try {

            engine.eval(scriptReader);
            retrieveScriptVariables();

        } catch (ScriptException se) {
            //plugin script failed.
            System.out.println(se);
            se.printStackTrace();
            handleScriptFailure(se);

        } finally {
            try {
                scriptReader.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }

    }

    /**
     * engine.put("teammates", teamMates.toString());
     */
    public abstract void setupVariableBindings();

    /**
     * String attributesToAddJson = (String) engine.getBindings(ScriptContext.ENGINE_SCOPE).get("attributesToAdd");
     *
     * org.json.JSONArray array = new org.json.JSONArray(attributesToAddJson);
     */
    public abstract void retrieveScriptVariables();

    public abstract void handleScriptFailure(ScriptException se);

    public ScriptEngine getEngine() {
        return engine;
    }

    public void setEngine(ScriptEngine engine) {
        this.engine = engine;
    }

    public Long getExternalReferenceId() {
        return externalReferenceId;
    }

    public void setExternalReferenceId(Long externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }
}
