package com.furbitobet.backend.repository;

import com.furbitobet.backend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    
    List<Player> findByTeam(String team);
    
    // Buscar jugadores por nombre (parcial, case insensitive)
    List<Player> findByNameContainingIgnoreCase(String name);
    
    // Top goleadores de un equipo
    @Query("SELECT p FROM Player p WHERE p.team = :team ORDER BY p.goals DESC")
    List<Player> findTopScorersByTeam(@Param("team") String team);
    
    // Top goleadores general (limitado)
    @Query("SELECT p FROM Player p ORDER BY p.goals DESC")
    List<Player> findTopScorers();
    
    // Top asistentes de un equipo
    @Query("SELECT p FROM Player p WHERE p.team = :team ORDER BY p.assists DESC")
    List<Player> findTopAssistersByTeam(@Param("team") String team);
    
    // Top asistentes general
    @Query("SELECT p FROM Player p ORDER BY p.assists DESC")
    List<Player> findTopAssisters();
    
    // Jugadores con mejor ratio de goles (mínimo X partidos)
    @Query("SELECT p FROM Player p WHERE p.matchesPlayed >= :minMatches " +
           "ORDER BY (CAST(p.goals AS double) / CAST(p.matchesPlayed AS double)) DESC")
    List<Player> findByBestGoalRate(@Param("minMatches") int minMatches);
    
    // Jugadores con mejor ratio de asistencias (mínimo X partidos)
    @Query("SELECT p FROM Player p WHERE p.matchesPlayed >= :minMatches " +
           "ORDER BY (CAST(p.assists AS double) / CAST(p.matchesPlayed AS double)) DESC")
    List<Player> findByBestAssistRate(@Param("minMatches") int minMatches);
    
    // Jugadores con más tarjetas amarillas
    @Query("SELECT p FROM Player p WHERE p.team = :team ORDER BY p.yellowCards DESC")
    List<Player> findMostBookedByTeam(@Param("team") String team);
    
    // Jugadores con tarjetas rojas
    @Query("SELECT p FROM Player p WHERE p.redCards > 0 ORDER BY p.redCards DESC")
    List<Player> findPlayersWithRedCards();
    
    // Contar jugadores por equipo
    long countByTeam(String team);
    
    // Jugadores que son habituales titulares (ratio alto de titularidades)
    @Query("SELECT p FROM Player p WHERE p.matchesPlayed > 0 " +
           "AND (CAST(p.matchesStarted AS double) / CAST(p.matchesPlayed AS double)) >= :minRatio")
    List<Player> findRegularStarters(@Param("minRatio") double minRatio);
    
    // Obtener todos los equipos únicos
    @Query("SELECT DISTINCT p.team FROM Player p WHERE p.team IS NOT NULL ORDER BY p.team")
    List<String> findAllTeams();
}
