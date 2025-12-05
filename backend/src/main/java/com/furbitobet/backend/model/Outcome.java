package com.furbitobet.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "outcomes")
public class Outcome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private BigDecimal odds;
    private String outcomeGroup;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("outcomes")
    private Event event;

    @Enumerated(EnumType.STRING)
    private OutcomeStatus status;

    public enum OutcomeStatus {
        PENDING, WON, LOST, VOID
    }
}
