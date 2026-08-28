package com.annie.campus_books.dto.response;

import com.annie.campus_books.model.User;

import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;

public record LoginResponse(String accessToken, String refreshToken,
                            String account, Collection<? extends GrantedAuthority> role,
                            Date accessTokenExpiration,
                            Instant refreshTokenExpiration) {

    public LoginResponse(GenerateTokenResponse generateTokenResponse, User user) {
        this(generateTokenResponse.accessToken(),
                generateTokenResponse.refreshToken().getToken(),
                user.getAccount(),
                user.getAuthorities(),
                generateTokenResponse.accessTokenExpiration(),
                generateTokenResponse.refreshToken().getExpirationDate());
    }

}
