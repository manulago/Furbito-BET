package com.furbitobet.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(nullable = false)
    private String password;

    // SECURITY: Email should not be exposed in public endpoints like ranking
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    // SECURITY: Reset tokens must never be exposed
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String resetToken;

    // SECURITY: Reset tokens must have expiration to prevent brute force attacks
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.time.LocalDateTime resetTokenExpiry;

    private BigDecimal balance = new BigDecimal("100.00");

    @Transient
    private BigDecimal grossProfit;

    // SECURITY: Pending profile update fields must never be exposed
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String pendingUsername;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private String pendingEmail;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private String pendingPassword;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private String confirmationToken;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.time.LocalDateTime tokenExpiry;

    private java.time.LocalDateTime lastSpinTime;

    /**
     * null = legacy enabled (true)
     * true = enabled
     * false = disabled (pending verification)
     */
    private Boolean enabled;

    public enum Role {
        ADMIN, USER
    }
}
