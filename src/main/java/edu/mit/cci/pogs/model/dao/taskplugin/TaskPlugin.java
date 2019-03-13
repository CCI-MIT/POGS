package edu.mit.cci.pogs.model.dao.taskplugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskPlugin {


    private static Map<String, TaskPlugin> registeredPlugins = new HashMap<>();


    private String taskPluginName;

    private String pluginRootFolder;


    public TaskPlugin(String taskPluginName, String pluginRootFolder) {

        this.taskPluginName = taskPluginName;
        this.pluginRootFolder = pluginRootFolder;
    }

    public static TaskPlugin getTaskPlugin(String pluginId) {
        return registeredPlugins.get(pluginId);
    }

    public static List<TaskPlugin> getAllTaskPlugins() {
        return new ArrayList<>(registeredPlugins.values());
    }

    public static void addTaskPlugin(String pluginID, TaskPlugin sessionRunner) {
        if (registeredPlugins.get(pluginID) == null) {
            registeredPlugins.put(pluginID, sessionRunner);
        }
    }

    private static void removeTaskPlugin(Long pluginId) {
        if (registeredPlugins.get(pluginId) != null) {
            registeredPlugins.remove(pluginId);
        }
    }

    public String getPluginRootFolder() {
        return pluginRootFolder;
    }

    public void setPluginRootFolder(String pluginRootFolder) {
        this.pluginRootFolder = pluginRootFolder;
    }


    public boolean isScriptType(){
        return getTaskPluginProperties().getScoring().getScoreAttributeName()
                .equals(ScoringType.script.getId().toString());
    }
    public boolean isExternalService(){
        return getTaskPluginProperties().getScoring().getScoreAttributeName()
                .equals(ScoringType.externalService.getId().toString());
    }
    public String getTaskPluginName() {
        return taskPluginName;
    }

    public void setTaskPluginName(String taskPluginName) {
        this.taskPluginName = taskPluginName;
    }


    private String readFile(String filePath) {
        String ret = new String();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                ret += sCurrentLine + "\n";
            }

        } catch (IOException e) {
            return null;
        }
        return ret;
    }

    public boolean hasLibsDir() {
        File f = new File(this.pluginRootFolder + File.separatorChar + "libs");
        if (f == null) return false;
        return f.isDirectory();
    }

    public String getLibsDirContent() {
        File f = new File(this.pluginRootFolder + File.separatorChar + "libs");
        File[] files = f.listFiles();
        String ret = "";
        for (File jsFile : files) {
            ret += readFile(this.pluginRootFolder + File.separatorChar + "libs" + File.separatorChar + jsFile.getName());
        }

        return ret;
    }

    public TaskPluginProperties getTaskPluginProperties() {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            TaskPluginProperties taskPluginProperties = mapper.readValue(new File(
                    this.pluginRootFolder +  File.separatorChar + "pluginProperties.yml"
                    ),
                    TaskPluginProperties.class);

            return taskPluginProperties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getTaskPrimerJsContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskPrimer.js");
    }
    public String getTaskScoreJsContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskScore.js");
    }

    public String getTaskEditJsContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskEdit.js");
    }

    public String getTaskEditHtmlContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskEdit.html");
    }

    public String getTaskWorkHtmlContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskWork.html");
    }
    public String getTaskPrimerHtmlContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskPrimer.html");
    }

    public String getTaskBeforeWorkJsContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskBeforeWork.js");
    }
    public String getTaskAfterWorkJsContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskAfterWork.js");
    }

    public String getTaskWorkJsContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskWork.js");
    }

    public String getTaskCSSContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "task.css");
    }
}
