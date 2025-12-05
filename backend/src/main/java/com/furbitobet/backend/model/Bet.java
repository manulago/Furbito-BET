package com.furbitobet.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "bets")
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "bet_outcomes", joinColumns = @JoinColumn(name = "bet_id"), inverseJoinColumns = @JoinColumn(name = "outcome_id"))
    private java.util.List<Outcome> outcomes;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private BetStatus status;

    public enum BetStatus {
        PENDING, WON, LOST, CANCELLED, VOID
    }
}
