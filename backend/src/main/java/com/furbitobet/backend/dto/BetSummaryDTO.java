package com.furbitobet.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class BetSummaryDTO {
    private Long id;
    private BigDecimal amount;
    private BigDecimal potentialWinnings;
    private String status;
    private List<String> outcomes;

    public BetSummaryDTO(Long id, BigDecimal amount, BigDecimal potentialWinnings, String status,
            List<String> outcomes) {
        this.id = id;
        this.amount = amount;
        this.potentialWinnings = potentialWinnings;
        this.status = status;
        this.outcomes = outcomes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPotentialWinnings() {
        return potentialWinnings;
    }

    public void setPotentialWinnings(BigDecimal potentialWinnings) {
        this.potentialWinnings = potentialWinnings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<String> outcomes) {
        this.outcomes = outcomes;
    }
}
