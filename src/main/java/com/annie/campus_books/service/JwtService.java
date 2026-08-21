package com.annie.campus_books.service;

import com.annie.campus_books.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

public class JwtService {

    private final SecretKey secretKey;
    private final int validSeconds;
    private final JwtParser jwtParser;

    public JwtService(String secretKeyStr, int validSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
        this.validSeconds = validSeconds;
        this.jwtParser=Jwts.parser().verifyWith(secretKey).build();
    }

    public String generateLoginAccessToken(User user) {
        long expirationMS = Instant.now().plusSeconds(validSeconds).getEpochSecond()*1000;

        Claims claims = Jwts.claims().
                issuedAt(new Date()).
                expiration(new Date(expirationMS)).
                add("account",user.getAccount()).
                add("role",user.getRole()).
                add("authorities",user.getAuthorities()).
                build();

        return Jwts.
                builder().
                claims(claims).
                signWith(secretKey).
                compact();
    }

    public Claims parseToken(String jwt)throws JwtException {
        return jwtParser.parseSignedClaims(jwt).getPayload();
    }
}
