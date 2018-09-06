package edu.mit.cci.pogs.listeners;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;


@Component
public class ApplicationStartup {


    @Value( "${plugins.dir}" )
    private String pathToPlugins;
    /**
     * This method is called during Spring's startup.
     *
     * @param event Event raised when an ApplicationContext gets initialized or refreshed.
     */

    public void onApplicationEvent(final ContextRefreshedEvent event) {
            //change this path
        try {
            File file = new ClassPathResource("countries.xml").getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        Resource resource = new ClassPathResource("plugins");
        File file = null;
        try {
            file = resource.getFile();
        } catch (IOException e) {
            file = new File(pathToPlugins);
        }
        String[] plugins = file.list();
        for(String p:plugins) {
            TaskPlugin tp = new TaskPlugin(p, file.getAbsolutePath()+ File.separatorChar+ p);
            TaskPlugin.addTaskPlugin(tp.getTaskPluginName(), tp);

        }

    }

} //

