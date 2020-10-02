package com.data.dataxer.security;
import com.data.dataxer.security.service.FirebaseUserDetailService;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private  EntryPointUnauthorizedHandler unauthorizedHandler;
    private  FirebaseUserDetailService firebaseUserDetailService;
    private  FirebaseAuth firebaseAuth;

    public WebSecurity(EntryPointUnauthorizedHandler unauthorizedHandler, FirebaseUserDetailService firebaseUserDetailService, FirebaseAuth firebaseAuth) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.firebaseUserDetailService = firebaseUserDetailService;
        this.firebaseAuth = firebaseAuth;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public FirebaseAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        FirebaseAuthenticationTokenFilter authenticationTokenFilter = new FirebaseAuthenticationTokenFilter(firebaseUserDetailService, firebaseAuth);
        authenticationTokenFilter.setAuthenticationManager(authenticationManager());
        return authenticationTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .requestMatchers()
                .antMatchers("/api/**")
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/file/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/storage/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/storage/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/pdf/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .failureHandler((request, response, exception) -> response.setStatus(401))
                .and()
                .csrf()
                .disable()
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }
}
