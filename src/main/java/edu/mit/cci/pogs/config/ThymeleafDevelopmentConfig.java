package edu.mit.cci.pogs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Profile("development")
@Configuration
public class ThymeleafDevelopmentConfig {

    private ThymeleafProperties thymeleafProperties;

    @Autowired
    public ThymeleafDevelopmentConfig(ThymeleafProperties thymeleafProperties) {
        this.thymeleafProperties = thymeleafProperties;
    }

    @Bean
    public ITemplateResolver templateResolver() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setSuffix(thymeleafProperties.getSuffix());
        resolver.setPrefix("src/main/resources/templates/");
        resolver.setTemplateMode(thymeleafProperties.getMode());
        resolver.setCharacterEncoding(thymeleafProperties.getEncoding().toString());
        resolver.setCacheable(thymeleafProperties.isCache());
        return resolver;
    }
}
