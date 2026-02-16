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

    // Posición del jugador (útil para calcular probabilidades)
    private String position;

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

    // Estadísticas adicionales para cálculos más precisos
    @Column(name = "minutes_played")
    private int minutesPlayed;

    @Column(name = "shots_on_target")
    private int shotsOnTarget;

    @Column(name = "key_passes")
    private int keyPasses;

    // Ratio de conversión de goles (goles/tiros)
    @Transient
    public double getGoalConversionRate() {
        if (shotsOnTarget == 0) return 0.0;
        return (double) goals / shotsOnTarget;
    }

    // Ratio de goles por 90 minutos
    @Transient
    public double getGoalsPer90() {
        if (minutesPlayed == 0) return 0.0;
        return (double) goals / (minutesPlayed / 90.0);
    }

    // Ratio de asistencias por 90 minutos
    @Transient
    public double getAssistsPer90() {
        if (minutesPlayed == 0) return 0.0;
        return (double) assists / (minutesPlayed / 90.0);
    }

    // Ratio de participación en goles (goles + asistencias) por partido
    @Transient
    public double getGoalInvolvementRate() {
        if (matchesPlayed == 0) return 0.0;
        return (double) (goals + assists) / matchesPlayed;
    }
}
