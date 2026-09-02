package com.annie.campus_books.dto.response;

import com.annie.campus_books.model.RefreshToken;
import com.annie.campus_books.model.User;

import java.time.Instant;

public record GenerateTokenResponse(String accessToken, Instant accessTokenExpiration,
                                    String refreshToken, Instant refreshTokenExpiration,
                                    String account, User.Role role) {
}
