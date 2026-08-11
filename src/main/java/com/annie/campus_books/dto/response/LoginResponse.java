package com.annie.campus_books.dto.response;

import com.annie.campus_books.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;

@Getter
public class LoginResponse {
    private String jwt;
    private String account;
    private User.Role role;
    private Collection<? extends GrantedAuthority> authorities;
    private Date expiration;

    public static LoginResponse getResponseOf( User user,String jwt,Date expiration) {
        LoginResponse loginResponse=new LoginResponse();
        loginResponse.jwt=jwt;
        loginResponse.account=user.getAccount();
        loginResponse.role=user.getRole();
        loginResponse.authorities=user.getAuthorities();
        loginResponse.expiration=expiration;
        return loginResponse;
    }

}
