package com.furbitobet.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class EventResultDTO {
    private String username;
    private BigDecimal totalWagered;
    private BigDecimal totalWon;
    private List<BetSummaryDTO> bets;

    public EventResultDTO(String username, BigDecimal totalWagered, BigDecimal totalWon, List<BetSummaryDTO> bets) {
        this.username = username;
        this.totalWagered = totalWagered;
        this.totalWon = totalWon;
        this.bets = bets;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getTotalWagered() {
        return totalWagered;
    }

    public void setTotalWagered(BigDecimal totalWagered) {
        this.totalWagered = totalWagered;
    }

    public BigDecimal getTotalWon() {
        return totalWon;
    }

    public void setTotalWon(BigDecimal totalWon) {
        this.totalWon = totalWon;
    }

    public List<BetSummaryDTO> getBets() {
        return bets;
    }

    public void setBets(List<BetSummaryDTO> bets) {
        this.bets = bets;
    }
}
