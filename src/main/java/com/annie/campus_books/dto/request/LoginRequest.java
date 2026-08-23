package com.annie.campus_books.dto.request;

public record LoginRequest(String account, String password) {

    public LoginRequest() {
        this("test-account", "test-password");
    }

}
