package com.annie.campus_books.dto.response;

import com.annie.campus_books.model.RefreshToken;

import java.util.Date;

public record GenerateTokenResponse(String accessToken, Date accessTokenExpiration, RefreshToken refreshToken) {
}
