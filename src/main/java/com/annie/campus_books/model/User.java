package com.annie.campus_books.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
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
        ADMIN,USER
    }

}
