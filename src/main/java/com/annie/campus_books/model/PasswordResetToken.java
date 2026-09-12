package com.annie.campus_books.model;

import jakarta.persistence.*;

import lombok.Data;

import java.time.Instant;

@Table(name = "password_reset_token")
@Data
@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_account", referencedColumnName = "account")
    private User user;

    @Column(unique = true)
    private String token;
    @Column
    private Instant expirationDate;

    public boolean isExpired() {
        return expirationDate.isBefore(Instant.now());
    }

}
