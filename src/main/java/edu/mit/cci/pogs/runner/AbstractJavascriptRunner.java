package edu.mit.cci.pogs.runner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

public abstract class AbstractJavascriptRunner {

    private ScriptEngine engine;

    private CompletedTask completedTask;

    public void runScript(String code, CompletedTask completedTask) {

        this.completedTask = completedTask;

        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = factory.getScriptEngine();

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

    public CompletedTask getCompletedTask() {
        return completedTask;
    }

    public void setCompletedTask(CompletedTask completedTask) {
        this.completedTask = completedTask;
    }
}
