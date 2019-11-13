package com.data.dataxer.security.jwt;

import com.data.dataxer.security.model.UserAuthenticationDetails;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtils implements Serializable {
    @Value("${dataxer.jwt.secret}")
    private String SECRET;

    @Value("${dataxer.jwt.security.token.expiration-time-in-mins}")
    private Long EXPIRATION_TIME;

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        try {
            return getClaimsFromToken(token).getSubject();
        } catch (Exception e) {
            // do nothing
        }

        return null;
    }

    //retrieve expiration date from jwt token
    private Date getExpirationDateFromToken(String token) {
        try {
            return getClaimsFromToken(token).getExpiration();
        } catch (Exception e) {
            // do nothing
        }

        return null;
    }

    //for retrieveing any information from token we will need the secret key
    private Claims getClaimsFromToken(String token) throws Exception {
        return Jwts.parser()
                .setSigningKey(Base64.getEncoder().encodeToString(SECRET.getBytes()))
                .parseClaimsJws(token)
                .getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(UserAuthenticationDetails userAuthenticationDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userAuthenticationDetails.getUsername());
    }

    //generate current date
    private Date generateCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    ///generate expiration time
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(generateCurrentDate())
                .setExpiration(generateExpirationDate())
                .setHeaderParam("typ", "JWT")
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(SECRET.getBytes()))
                .compact();
    }

    //validate token
    public Boolean validateToken(String token, UserAuthenticationDetails userAuthenticationDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userAuthenticationDetails.getUsername()) && !isTokenExpired(token));
    }
}

