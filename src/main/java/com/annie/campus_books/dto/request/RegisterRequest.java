package com.annie.campus_books.dto.request;

public record RegisterRequest(String account, String email, String address, String password, String confirmPassword) {

    public RegisterRequest() {
        this("test-account", "test-email", "test-address",
                "test-password", "test-password");
    }

}
