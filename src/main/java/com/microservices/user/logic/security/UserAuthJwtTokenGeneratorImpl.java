package com.microservices.user.logic.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.microservices.user.core.crypto.UserAuthTokenGeneratorService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;



@Component
public class UserAuthJwtTokenGeneratorImpl implements UserAuthTokenGeneratorService {
    
    @Value("${photosite_token_key}")
    private String key;
    
    @Override
    public String generate(Long userId) {
        
        String token = Jwts.builder()
            .setSubject(userId.toString())
            .signWith(SignatureAlgorithm.HS256, key)
            .compact();
        
        return token;
    }
    
    
    @Override
    public String generate(Long userId, String sessionId) {
        String token = Jwts.builder()
                .setSubject(userId.toString())
                .claim("SESSION_ID", sessionId)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
            
            return token;
    }
    
    @Override
    public Long getUserId(String token) {
        try {
            String subj = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
            return Long.parseLong(subj); 
        }
        catch(Exception exception) {
            return null;
        }
    }
}
