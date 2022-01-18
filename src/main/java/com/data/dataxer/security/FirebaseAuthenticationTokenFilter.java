package com.data.dataxer.security;

import com.data.dataxer.security.service.FirebaseUserDetailService;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class FirebaseAuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {
    private final FirebaseUserDetailService firebaseUserDetailService;
    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthenticationTokenFilter(FirebaseUserDetailService firebaseUserDetailService, FirebaseAuth firebaseAuth) {
        this.firebaseUserDetailService = firebaseUserDetailService;
        this.firebaseAuth = firebaseAuth;
    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authToken = httpRequest.getHeader("Authorization");

        if (authToken != null) {
            FirebaseToken firebaseToken = null;


            try {
                firebaseToken = firebaseAuth.verifyIdToken(authToken);
            } catch (FirebaseAuthException e) {
                e.printStackTrace();
            }

            if (firebaseToken != null /*&& firebaseToken.isEmailVerified()*/) {
                UserDetails userDetails = firebaseUserDetailService.loadOrCreateUser(firebaseToken);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
