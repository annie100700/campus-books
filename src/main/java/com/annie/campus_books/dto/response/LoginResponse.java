package com.annie.campus_books.dto.response;

import com.annie.campus_books.model.User;
import lombok.Getter;

@Getter
public class LoginResponse {
    private String jwt;
    private String account;
    private User.Role role;

    public static LoginResponse getResponseOf(String jwt, User user) {
        LoginResponse loginResponse=new LoginResponse();
        loginResponse.jwt=jwt;
        loginResponse.account=user.getAccount();
        loginResponse.role=user.getRole();
        return loginResponse;
    }
}
