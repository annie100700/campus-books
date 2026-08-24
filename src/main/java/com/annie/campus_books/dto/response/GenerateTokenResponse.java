package com.annie.campus_books.dto.response;

import java.util.Date;

public record GenerateTokenResponse(String jwt, Date expiration) {
}
