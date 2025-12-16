package com.furbitobet.backend.dto;

import com.furbitobet.backend.model.User;
import java.math.BigDecimal;

/**
 * DTO for user information in authentication responses.
 * Only exposes safe, non-sensitive user data.
 */
public class UserDTO {
    private Long id;
    private String username;
    private User.Role role;
    private BigDecimal balance;
    private Boolean enabled;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.balance = user.getBalance();
        this.enabled = user.getEnabled();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public User.Role getRole() {
        return role;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
