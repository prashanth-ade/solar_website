package com.solarflow.security;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets; import java.util.*;
@Service public class JwtService {
 @Value("${app.jwtSecret}") String secret; @Value("${app.jwtExpirationMs:86400000}") long expiration;
 private javax.crypto.SecretKey key(){return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}
 public String issue(String email,String role){return Jwts.builder().subject(email).claim("role",role).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+expiration)).signWith(key()).compact();}
 public Claims parse(String token){return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();}
}
