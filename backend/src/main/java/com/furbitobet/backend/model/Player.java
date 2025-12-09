package com.furbitobet.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String team;

    // Statistics
    private int goals;
    private int assists;

    @Column(name = "matches_played")
    private int matchesPlayed;

    @Column(name = "matches_started")
    private int matchesStarted;

    @Column(name = "yellow_cards")
    private int yellowCards;

    @Column(name = "red_cards")
    private int redCards;
}
