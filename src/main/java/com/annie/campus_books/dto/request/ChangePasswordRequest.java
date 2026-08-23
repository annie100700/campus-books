package com.annie.campus_books.dto.request;

public record ChangePasswordRequest(String oldPassword, String newPassword, String confirmNewPassword) {

    public ChangePasswordRequest() {
        this("test-old-password", "test-new-password", "test-new-password");
    }

}
