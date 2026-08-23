package com.annie.campus_books.dto.response;

import com.annie.campus_books.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;

public record LoginResponse(String jwt, String account, Collection<? extends GrantedAuthority> role, Date expiration) {

    public LoginResponse(String jwt, User user, Date expiration) {
        this(jwt, user.getAccount(), user.getRole(), expiration);
    }

}
