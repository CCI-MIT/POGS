package edu.mit.cci.pogs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders
        .AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration
        .WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private AuthUserDetailsService authUserDetailsService;

    @Autowired
    public WebSecurityConfig(AuthUserDetailsService authUserDetailsService) {
        this.authUserDetailsService = authUserDetailsService;
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/vendor/**");
    }
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity

                .authorizeRequests()
                    .antMatchers("/admin/**").authenticated()
                    //.anyRequest().permitAll()
                    .and()
                .authorizeRequests()
                    .antMatchers("/images/**").permitAll()
                    .and()
                .authorizeRequests()
                    .antMatchers("/css/**").permitAll()
                    .and()
                .authorizeRequests()
                    .antMatchers("/vendor/**").permitAll()
                    .and()
                .authorizeRequests()
                    .antMatchers("/img/**").permitAll()
                    .and()
                .authorizeRequests()
                    .antMatchers("/display_name/**").permitAll()
                    .and()
                .authorizeRequests().antMatchers(HttpMethod.POST,"/jitsiwebhook").permitAll().and()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("emailAddress")
                    .permitAll()
                    .and()
                .logout()
                    .permitAll()
                    .and()
                .csrf()
                .ignoringAntMatchers("/jitsiwebhook")
                    .and()
                .headers().defaultsDisabled()
                    .referrerPolicy(ReferrerPolicy.ORIGIN_WHEN_CROSS_ORIGIN)
                        .and()
                    .contentTypeOptions()
                        .and()
                    .xssProtection()
                        .block(true)
                        .and()
                    .frameOptions()
                        .sameOrigin();

        //.frameOptions()
        //        .disable()
        //        .addHeaderWriter(new StaticHeadersWriter("X-FRAME-OPTIONS",
         //               "ALLOW-FROM https://www.youtube.com/watch?v=HV2LVEPrKGs&feature=emb_title"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }
}
