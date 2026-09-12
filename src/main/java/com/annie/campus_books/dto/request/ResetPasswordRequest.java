package com.annie.campus_books.dto.request;

public record ResetPasswordRequest(String passwordResetToken, String newPassword, String confirmPassword) {
}
