package edu.mit.cci.pogs.listeners;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;


@Component
public class ApplicationStartup {


    @Autowired
    private Environment env;


    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        Resource resource = new ClassPathResource("plugins");
        File file;
        try {
            file = resource.getFile();
        } catch (IOException e) {
            String pathToPlugins = env.getProperty("plugin.dir");

            file = new File(pathToPlugins);
        }
        String[] plugins = file.list();
        for(String p:plugins) {
            TaskPlugin tp = new TaskPlugin(p, file.getAbsolutePath()+ File.separatorChar+ p);
            TaskPlugin.addTaskPlugin(tp.getTaskPluginName(), tp);

        }

        System.out.println("   ==================================================================    ");
        System.out.println("   ==================================================================    ");
        System.out.println("   ===                  POGS Version 1.12.17                      ===    ");
        System.out.println("   ==================================================================    ");
        System.out.println("   ==================================================================    ");

    }

}

