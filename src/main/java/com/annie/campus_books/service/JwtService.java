package com.annie.campus_books.service;

import com.annie.campus_books.dto.response.GenerateTokenResponse;
import com.annie.campus_books.model.PasswordResetToken;
import com.annie.campus_books.model.RefreshToken;
import com.annie.campus_books.model.User;
import com.annie.campus_books.repository.PasswordResetTokenRepo;
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

public class JwtService {

    private final SecretKey secretKey;
    private final long accessValidMillis;
    private final JwtParser jwtParser;

    private final long refreshValidMillis;
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    private final long passwordResetValidMillis;
    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;

    public JwtService(String secretKeyStr, long accessValidMillis, long refreshValidMillis, long passwordResetValidMillis) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
        this.accessValidMillis = accessValidMillis;
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
        this.refreshValidMillis = refreshValidMillis;
        this.passwordResetValidMillis = passwordResetValidMillis;
    }

    public GenerateTokenResponse generateToken(User user) {
        Instant expirationAt = Instant.now().plusMillis(accessValidMillis);

        Claims claims = Jwts.claims().
                issuedAt(new Date()).
                expiration(Date.from(expirationAt)).
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
        Instant expirationAt = Instant.now().plusMillis(refreshValidMillis);

        Claims claims = Jwts.claims().
                issuedAt(new Date()).
                expiration(Date.from(expirationAt)).
                add("account", user.getAccount()).
                add("role", user.getRole()).
                build();

        String tokenStr = Jwts.
                builder().
                claims(claims).
                signWith(secretKey).
                compact();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(tokenStr);
        refreshToken.setExpirationDate(expirationAt);
        return refreshTokenRepo.save(refreshToken);
    }

    @Transactional
    public GenerateTokenResponse refreshToken(RefreshToken refreshToken) {
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

    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepo.delete(refreshToken);
    }

    @Transactional
    public void deleteTokenByUser(User user) {
        refreshTokenRepo.deleteAllByUser(user);
    }

    public PasswordResetToken generatePasswordResetToken(User user) {
        Instant expirationAt = Instant.now().plusMillis(passwordResetValidMillis);

        Claims claims = Jwts.claims().
                issuedAt(new Date()).
                expiration(Date.from(expirationAt)).
                add("account", user.getAccount()).
                add("role", user.getRole()).
                build();

        String tokenStr = Jwts.
                builder().
                claims(claims).
                signWith(secretKey).
                compact();

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(tokenStr);
        passwordResetToken.setExpirationDate(expirationAt);
        return passwordResetTokenRepo.save(passwordResetToken);
    }

    public PasswordResetToken getPasswordResetToken(String tokenStr) {
        return passwordResetTokenRepo.findByToken(tokenStr).orElse(null);
    }

    public void deleteResetPasswordToken(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepo.delete(passwordResetToken);
    }
}
