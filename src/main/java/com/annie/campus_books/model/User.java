package com.annie.campus_books.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class User implements UserDetails {

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
        ADMIN,USER
    }

    private boolean deleted;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return account;
    }
}
