package com.furbitobet.backend.dto;

import java.math.BigDecimal;

public class WinningBetDTO {
    private String username;
    private BigDecimal amount;
    private BigDecimal winnings;

    public WinningBetDTO(String username, BigDecimal amount, BigDecimal winnings) {
        this.username = username;
        this.amount = amount;
        this.winnings = winnings;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getWinnings() {
        return winnings;
    }

    public void setWinnings(BigDecimal winnings) {
        this.winnings = winnings;
    }
}
