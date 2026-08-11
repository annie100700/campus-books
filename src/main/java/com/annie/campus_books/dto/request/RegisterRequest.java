package com.annie.campus_books.dto.request;

public record RegisterRequest(String account,String email,String address,String password,String confirmPassword) {
}
