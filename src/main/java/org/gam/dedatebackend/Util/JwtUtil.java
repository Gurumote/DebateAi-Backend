package org.gam.dedatebackend.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final UserDetailsService userDetailsService;

    @Value("${jwt.token.key}")
    private String key;

    public JwtUtil(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String getToken(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();//UserDetails → Spring Security object representing the logged-in user.
        return createToke(claims,userDetails.getUsername());
    }

    private String createToke(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }
    public  Claims extractAllclaims(String token){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims= extractAllclaims(token);
        return claimsResolver.apply(claims);
    }
    public String extractEmail(String token){
        return extractClaims(token, Claims::getSubject);
    }
    public Date extractExpiration(String token){
        return  extractClaims(token,Claims::getExpiration);
    }
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public boolean validateToken(String token ,UserDetails userDetails){
        final String email=extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
