package com.annie.campus_books.service;

import com.annie.campus_books.dto.response.GenerateTokenResponse;
import com.annie.campus_books.model.RefreshToken;
import com.annie.campus_books.model.User;
import com.annie.campus_books.repository.RefreshTokenRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class JwtService {

    private final SecretKey secretKey;
    private final int validSeconds;
    private final JwtParser jwtParser;

    public JwtService(String secretKeyStr, int validSeconds) {
    private final int refreshValidSecond;
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

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
    public RefreshToken generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);

        String tokenStr = UUID.randomUUID().toString();
        refreshToken.setToken(tokenStr);

        Instant expirationAt = Instant.now().plusSeconds(refreshValidSecond);
        refreshToken.setExpirationDate(expirationAt);
        return refreshTokenRepo.save(refreshToken);
    }

    public Claims parseToken(String jwt) throws JwtException {
        return jwtParser.parseSignedClaims(jwt).getPayload();
    }

}
