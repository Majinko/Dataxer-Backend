package com.data.dataxer.security;

import com.data.dataxer.security.jwt.JwtTokenUtils;
import com.data.dataxer.security.model.UserAuthenticationDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService userDetailsService;

    public AuthenticationTokenFilter(JwtTokenUtils jwtTokenUtils, UserDetailsService userDetailsService) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authToken = extractAuthToken(httpRequest.getHeader("Authorization"));

        if (authToken == null) {
            authToken = httpRequest.getHeader("api_key");
        }

        final String username = jwtTokenUtils.getUsernameFromToken(authToken);

        if (username != null && isNotAuthenticated(SecurityContextHolder.getContext().getAuthentication())) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtils.validateToken(authToken, (UserAuthenticationDetails) userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(
                    new AnonymousAuthenticationToken("anonymousToken", "anonymous", anonymousAuthority()));
        }

        chain.doFilter(request, response);
    }

    private List<GrantedAuthority> anonymousAuthority() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        return authorities;
    }

    private String extractAuthToken(String authHeader) {
        if (authHeader != null) {
            Pattern p = Pattern.compile("[Bb]earer (.*)");
            Matcher m = p.matcher(authHeader);

            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    private boolean isNotAuthenticated(Authentication authentication) {
        return authentication == null || !(authentication.isAuthenticated()) ||
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
    }
}
