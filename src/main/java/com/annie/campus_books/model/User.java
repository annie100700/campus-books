package com.annie.campus_books.model;

import jakarta.persistence.*;

import lombok.Data;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Data
@Entity
public class User {

    @Id
    private String account;
    @Column(nullable = false)
    private String password;
    private String email;
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ADMIN, USER
    }

    private boolean deleted;

    public Collection<? extends GrantedAuthority> getRole() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

}
