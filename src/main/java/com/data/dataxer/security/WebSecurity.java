package com.data.dataxer.security;

import com.data.dataxer.security.jwt.JwtTokenUtils;
import com.data.dataxer.security.service.JwtUserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private final EntryPointUnauthorizedHandler unauthorizedHandler;
    private final JwtUserDetailsServiceImpl jwtUserDetailsServiceImpl;
    private final JwtTokenUtils jwtTokenUtils;

    public WebSecurity(EntryPointUnauthorizedHandler unauthorizedHandler, JwtUserDetailsServiceImpl jwtUserDetailsServiceImpl, JwtTokenUtils jwtTokenUtils) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUserDetailsServiceImpl = jwtUserDetailsServiceImpl;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsServiceImpl).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        AuthenticationTokenFilter filter = new AuthenticationTokenFilter(jwtTokenUtils, jwtUserDetailsServiceImpl);
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
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
                //.antMatchers("**").permitAll()
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
