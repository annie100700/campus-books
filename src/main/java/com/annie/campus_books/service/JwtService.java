package com.annie.campus_books.service;

import com.annie.campus_books.dto.response.GenerateTokenResponse;
import com.annie.campus_books.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
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
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    public GenerateTokenResponse generateToken(User user) {
        Date expirationAt = new Date(Instant.now().plusSeconds(validSeconds).getEpochSecond() * 1000);

        Claims claims = Jwts.claims().
                issuedAt(new Date()).
                expiration(expirationAt).
                add("account", user.getAccount()).
                add("role", user.getRole()).
                build();

        String jwt = Jwts.
                builder().
                claims(claims).
                signWith(secretKey).
                compact();

        return new GenerateTokenResponse(jwt, expirationAt);
    }

    public Claims parseToken(String jwt) throws JwtException {
        return jwtParser.parseSignedClaims(jwt).getPayload();
    }

}
