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

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String resetToken;

    private BigDecimal balance = new BigDecimal("100.00");

    @Transient
    private BigDecimal grossProfit;

    // Fields for profile updates
    private String pendingUsername;
    private String pendingEmail;
    private String pendingPassword;
    private String confirmationToken;
    private java.time.LocalDateTime tokenExpiry;

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
