package edu.mit.cci.pogs.model.dao.taskplugin;

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

    public static TaskPlugin getTaskPlugin(String sessionId) {
        return registeredPlugins.get(sessionId);
    }

    public static List<TaskPlugin> getAllTaskPlugins() {
        return new ArrayList<>(registeredPlugins.values());
    }

    public static void addTaskPlugin(String sessionId, TaskPlugin sessionRunner) {
        if (registeredPlugins.get(sessionId) == null) {
            registeredPlugins.put(sessionId, sessionRunner);
        }
    }

    private static void removeTaskPlugin(Long sessionId) {
        if (registeredPlugins.get(sessionId) != null) {
            registeredPlugins.remove(sessionId);
        }
    }

    public String getPluginRootFolder() {
        return pluginRootFolder;
    }

    public void setPluginRootFolder(String pluginRootFolder) {
        this.pluginRootFolder = pluginRootFolder;
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


    public String getTaskEditJsContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskEdit.js");
    }

    public String getTaskEditHtmlContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskEdit.html");
    }

    public String getTaskWorkHtmlContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskWork.html");
    }

    public String getTaskWorkJsContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "taskWork.js");
    }

    public String getTaskCSSContent() {
        return readFile(this.pluginRootFolder + File.separatorChar + "task.css");
    }
}
