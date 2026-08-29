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
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class JwtService {

    private final SecretKey secretKey;
    private final int accessValidSeconds;
    private final JwtParser jwtParser;

    private final int refreshValidSecond;
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    public JwtService(String secretKeyStr, int accessValidSeconds, int refreshValidSecond) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
        this.accessValidSeconds = accessValidSeconds;
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
        this.refreshValidSecond = refreshValidSecond;
    }

    public GenerateTokenResponse generateToken(User user) {
        Date expirationAt = new Date(Instant.now().plusSeconds(accessValidSeconds).getEpochSecond() * 1000);

        Claims claims = Jwts.claims().
                issuedAt(new Date()).
                expiration(expirationAt).
                add("account", user.getAccount()).
                add("role", user.getRole()).
                build();

        String accessToken = Jwts.
                builder().
                claims(claims).
                signWith(secretKey).
                compact();

        RefreshToken refreshToken = generateRefreshToken(user);
        return new GenerateTokenResponse(accessToken, expirationAt, refreshToken);
    }

    private RefreshToken generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);

        String tokenStr = UUID.randomUUID().toString();
        refreshToken.setToken(tokenStr);

        Instant expirationAt = Instant.now().plusSeconds(refreshValidSecond);
        refreshToken.setExpirationDate(expirationAt);
        return refreshTokenRepo.save(refreshToken);
    }

    @Transactional
    public GenerateTokenResponse refreshToken(String RefreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(RefreshTokenStr).
                orElseThrow(() -> new JwtException("Refresh token not found"));
        if (refreshToken.isExpired()) {
            refreshTokenRepo.delete(refreshToken);
            throw new JwtException("Refresh token expired");
        }
        User user = refreshToken.getUser();
        refreshTokenRepo.delete(refreshToken);
        return generateToken(user);
    }

    public Claims parseToken(String jwt) throws JwtException {
        return jwtParser.parseSignedClaims(jwt).getPayload();
    }

    public RefreshToken getRefreshToken(String RefreshTokenStr) {
        return refreshTokenRepo.findByToken(RefreshTokenStr).orElse(null);
    }

    public void deleteByToken(String refreshTokenStr) {
        refreshTokenRepo.deleteByToken(refreshTokenStr);
    }

}
