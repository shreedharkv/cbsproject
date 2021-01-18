package com.dfq.coeffi.cbs.configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author H Kapil Kumar
 */

public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/public").permitAll()
                .anyRequest().authenticated();
    }
}