package com.annie.campus_books.model;

import jakarta.persistence.*;

import lombok.Data;

import java.time.Instant;

@Table(name = "refresh_token")
@Data
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_account", referencedColumnName = "account", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private Instant expirationDate;

    public boolean isExpired() {
        return expirationDate.isBefore(Instant.now());
    }

}
