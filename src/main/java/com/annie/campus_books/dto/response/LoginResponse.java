package com.annie.campus_books.dto.response;

import com.annie.campus_books.model.User;

import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;

public record LoginResponse(String accessToken, String refreshToken,
                            String account, Collection<? extends GrantedAuthority> role,
                            Instant accessTokenExpiration,
                            Instant refreshTokenExpiration) {

    public LoginResponse(GenerateTokenResponse generateTokenResponse, User user) {
        this(generateTokenResponse.accessToken(),
                generateTokenResponse.refreshToken(),
                user.getAccount(),
                user.getAuthorities(),
                generateTokenResponse.accessTokenExpiration(),
                generateTokenResponse.refreshTokenExpiration());
    }

}
