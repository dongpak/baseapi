/*
 *
 */
package com.churchclerk.baseapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 */
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityEntry authenticationEntryPoint;

    @Autowired
    private SecurityFilter securityFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        boolean permitAll = false;
        if (permitAll) {
            http.csrf().disable().authorizeRequests().anyRequest().permitAll();
        }
        else {
            http.csrf().disable()
                    .authorizeRequests().antMatchers("/api/test/test").permitAll()
                    .anyRequest().authenticated().and()
                    .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }
}
