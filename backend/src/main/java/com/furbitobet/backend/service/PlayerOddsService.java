package com.furbitobet.backend.service;

import com.furbitobet.backend.model.Event;
import com.furbitobet.backend.model.Outcome;
import com.furbitobet.backend.model.Player;
import com.furbitobet.backend.repository.OutcomeRepository;
import com.furbitobet.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio especializado para el cálculo automático de cuotas de jugadores
 * basado en estadísticas de la base de datos.
 */
@Service
public class PlayerOddsService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerOddsService.class);

    // Constantes para el cálculo de cuotas
    private static final double HOUSE_MARGIN = 0.92; // Margen de la casa (8% de beneficio)
    private static final double MIN_PROBABILITY = 0.02; // 2% mínimo de probabilidad
    private static final double MAX_PROBABILITY = 0.85; // 85% máximo de probabilidad
    private static final int MIN_MATCHES_FOR_RELIABLE_STATS = 3; // Mínimo de partidos para estadísticas fiables

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private OutcomeRepository outcomeRepository;

    /**
     * Genera todas las cuotas de jugadores para un evento dado.
     * Analiza el nombre del evento para extraer los equipos y busca sus jugadores.
     */
    public List<Outcome> generateAllPlayerOdds(Event event) {
        List<Outcome> generatedOutcomes = new ArrayList<>();

        try {
            String[] teams = extractTeamsFromEvent(event.getName());
            if (teams == null || teams.length != 2) {
                logger.warn("No se pudieron extraer los equipos del evento: {}", event.getName());
                return generatedOutcomes;
            }

            String homeTeam = teams[0].trim();
            String awayTeam = teams[1].trim();

            List<Player> homePlayers = playerRepository.findByTeam(homeTeam);
            List<Player> awayPlayers = playerRepository.findByTeam(awayTeam);

            logger.info("Generando cuotas para {} jugadores de {} y {} jugadores de {}",
                    homePlayers.size(), homeTeam, awayPlayers.size(), awayTeam);

            List<Player> allPlayers = new ArrayList<>();
            allPlayers.addAll(homePlayers);
            allPlayers.addAll(awayPlayers);

            // Generar cuotas de goleadores
            generatedOutcomes.addAll(generateScorerOdds(event, allPlayers));

            // Generar cuotas de asistencias
            generatedOutcomes.addAll(generateAssistOdds(event, allPlayers));

            // Generar cuotas de tarjetas amarillas
            generatedOutcomes.addAll(generateYellowCardOdds(event, allPlayers));

            // Generar cuotas de tarjetas rojas
            generatedOutcomes.addAll(generateRedCardOdds(event, allPlayers));

            // Generar cuotas de primer goleador (top 5 de cada equipo)
            generatedOutcomes.addAll(generateFirstScorerOdds(event, homePlayers, awayPlayers));

            // Generar cuotas de gol + asistencia (jugadores destacados)
            generatedOutcomes.addAll(generateGoalAndAssistOdds(event, allPlayers));

            logger.info("Se generaron {} cuotas de jugadores para el evento {}", 
                    generatedOutcomes.size(), event.getName());

        } catch (Exception e) {
            logger.error("Error generando cuotas de jugadores para evento {}: {}", 
                    event.getName(), e.getMessage());
        }

        return generatedOutcomes;
    }

    /**
     * Genera cuotas de goleadores basándose en el ratio de goles por partido.
     */
    private List<Outcome> generateScorerOdds(Event event, List<Player> players) {
        List<Outcome> outcomes = new ArrayList<>();

        for (Player player : players) {
            BigDecimal odds = calculateGoalOdds(player);
            Outcome outcome = createOutcome(event, "Gol de " + player.getName(), odds, "Goleadores");
            outcomes.add(outcomeRepository.save(outcome));
        }

        return outcomes;
    }

    /**
     * Genera cuotas de asistencias basándose en el ratio de asistencias por partido.
     */
    private List<Outcome> generateAssistOdds(Event event, List<Player> players) {
        List<Outcome> outcomes = new ArrayList<>();

        for (Player player : players) {
            BigDecimal odds = calculateAssistOdds(player);
            Outcome outcome = createOutcome(event, "Asistencia de " + player.getName(), odds, "Asistencias");
            outcomes.add(outcomeRepository.save(outcome));
        }

        return outcomes;
    }

    /**
     * Genera cuotas de tarjetas amarillas.
     */
    private List<Outcome> generateYellowCardOdds(Event event, List<Player> players) {
        List<Outcome> outcomes = new ArrayList<>();

        // Solo incluir jugadores con historial de tarjetas o suficientes partidos
        List<Player> relevantPlayers = players.stream()
                .filter(p -> p.getMatchesPlayed() >= MIN_MATCHES_FOR_RELIABLE_STATS || p.getYellowCards() > 0)
                .collect(Collectors.toList());

        for (Player player : relevantPlayers) {
            BigDecimal odds = calculateYellowCardOdds(player);
            Outcome outcome = createOutcome(event, "Tarjeta amarilla para " + player.getName(), odds, "Tarjetas");
            outcomes.add(outcomeRepository.save(outcome));
        }

        return outcomes;
    }

    /**
     * Genera cuotas de tarjetas rojas.
     */
    private List<Outcome> generateRedCardOdds(Event event, List<Player> players) {
        List<Outcome> outcomes = new ArrayList<>();

        // Solo incluir jugadores con historial de tarjetas rojas
        List<Player> relevantPlayers = players.stream()
                .filter(p -> p.getRedCards() > 0 || p.getYellowCards() >= 3)
                .collect(Collectors.toList());

        for (Player player : relevantPlayers) {
            BigDecimal odds = calculateRedCardOdds(player);
            Outcome outcome = createOutcome(event, "Tarjeta roja para " + player.getName(), odds, "Tarjetas");
            outcomes.add(outcomeRepository.save(outcome));
        }

        return outcomes;
    }

    /**
     * Genera cuotas de primer goleador (top goleadores de cada equipo).
     */
    private List<Outcome> generateFirstScorerOdds(Event event, List<Player> homePlayers, List<Player> awayPlayers) {
        List<Outcome> outcomes = new ArrayList<>();

        // Obtener top 5 goleadores de cada equipo
        List<Player> topHomeSorers = homePlayers.stream()
                .sorted(Comparator.comparingDouble(this::getGoalRate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<Player> topAwayScorers = awayPlayers.stream()
                .sorted(Comparator.comparingDouble(this::getGoalRate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<Player> allTopScorers = new ArrayList<>();
        allTopScorers.addAll(topHomeSorers);
        allTopScorers.addAll(topAwayScorers);

        for (Player player : allTopScorers) {
            BigDecimal odds = calculateFirstScorerOdds(player, allTopScorers);
            Outcome outcome = createOutcome(event, player.getName() + " primer goleador", odds, "Primer Goleador");
            outcomes.add(outcomeRepository.save(outcome));
        }

        return outcomes;
    }

    /**
     * Genera cuotas para gol + asistencia en el mismo partido.
     */
    private List<Outcome> generateGoalAndAssistOdds(Event event, List<Player> players) {
        List<Outcome> outcomes = new ArrayList<>();

        // Solo jugadores con buen historial de participación en goles
        List<Player> relevantPlayers = players.stream()
                .filter(p -> p.getGoals() >= 2 && p.getAssists() >= 2)
                .sorted(Comparator.comparingDouble((Player p) -> getGoalRate(p) + getAssistRate(p)).reversed())
                .limit(10)
                .collect(Collectors.toList());

        for (Player player : relevantPlayers) {
            BigDecimal odds = calculateGoalAndAssistOdds(player);
            Outcome outcome = createOutcome(event, player.getName() + " marca y asiste", odds, "Especiales Jugador");
            outcomes.add(outcomeRepository.save(outcome));
        }

        return outcomes;
    }

    // ===== MÉTODOS DE CÁLCULO DE CUOTAS =====

    /**
     * Calcula las cuotas de gol para un jugador basándose en sus estadísticas.
     * Fórmula: Cuota = (1 / Probabilidad) * Margen de la casa
     */
    public BigDecimal calculateGoalOdds(Player player) {
        double probability = calculateGoalProbability(player);
        return probabilityToOdds(probability);
    }

    /**
     * Calcula la probabilidad de que un jugador marque gol.
     */
    private double calculateGoalProbability(Player player) {
        int matches = player.getMatchesPlayed();
        int goals = player.getGoals();

        if (matches == 0) {
            // Sin partidos, probabilidad base muy baja
            return MIN_PROBABILITY;
        }

        // Ratio base de goles por partido
        double baseRate = (double) goals / matches;

        // Ajuste por número de partidos (más partidos = más confianza)
        double confidenceFactor = Math.min(1.0, matches / 10.0);

        // Ajuste por partidos como titular (más titularidades = más oportunidades)
        double startingFactor = 1.0;
        if (player.getMatchesStarted() > 0) {
            startingFactor = (double) player.getMatchesStarted() / matches;
            startingFactor = 0.8 + (0.4 * startingFactor); // Entre 0.8 y 1.2
        }

        double probability = baseRate * confidenceFactor * startingFactor;

        // Aplicar mínimo para jugadores sin goles pero con partidos
        if (goals == 0 && matches >= MIN_MATCHES_FOR_RELIABLE_STATS) {
            probability = MIN_PROBABILITY * 1.5; // Ligeramente mayor que el mínimo absoluto
        }

        return clampProbability(probability);
    }

    /**
     * Calcula las cuotas de asistencia para un jugador.
     */
    public BigDecimal calculateAssistOdds(Player player) {
        double probability = calculateAssistProbability(player);
        return probabilityToOdds(probability);
    }

    /**
     * Calcula la probabilidad de que un jugador dé una asistencia.
     */
    private double calculateAssistProbability(Player player) {
        int matches = player.getMatchesPlayed();
        int assists = player.getAssists();

        if (matches == 0) {
            return MIN_PROBABILITY;
        }

        // Ratio base de asistencias por partido
        double baseRate = (double) assists / matches;

        // Ajuste por número de partidos
        double confidenceFactor = Math.min(1.0, matches / 10.0);

        // Ajuste por titularidades
        double startingFactor = 1.0;
        if (player.getMatchesStarted() > 0) {
            startingFactor = (double) player.getMatchesStarted() / matches;
            startingFactor = 0.8 + (0.4 * startingFactor);
        }

        double probability = baseRate * confidenceFactor * startingFactor;

        // Mínimo para jugadores sin asistencias
        if (assists == 0 && matches >= MIN_MATCHES_FOR_RELIABLE_STATS) {
            probability = MIN_PROBABILITY;
        }

        return clampProbability(probability);
    }

    /**
     * Calcula las cuotas de tarjeta amarilla para un jugador.
     */
    public BigDecimal calculateYellowCardOdds(Player player) {
        int matches = player.getMatchesPlayed();
        int yellowCards = player.getYellowCards();

        if (matches == 0) {
            return probabilityToOdds(MIN_PROBABILITY);
        }

        // Ratio de tarjetas amarillas por partido
        double baseRate = (double) yellowCards / matches;

        // Ajuste: si no tiene tarjetas pero ha jugado, probabilidad base
        if (yellowCards == 0) {
            baseRate = 0.05; // 5% base para cualquier jugador
        }

        double probability = clampProbability(baseRate);
        return probabilityToOdds(probability);
    }

    /**
     * Calcula las cuotas de tarjeta roja para un jugador.
     */
    public BigDecimal calculateRedCardOdds(Player player) {
        int matches = player.getMatchesPlayed();
        int redCards = player.getRedCards();
        int yellowCards = player.getYellowCards();

        if (matches == 0) {
            return probabilityToOdds(MIN_PROBABILITY);
        }

        // Ratio de tarjetas rojas por partido
        double baseRate = (double) redCards / matches;

        // Ajuste basado en historial de amarillas (más amarillas = más riesgo de roja)
        double yellowFactor = 1.0 + (0.1 * Math.min(yellowCards, 10) / matches);

        // Las rojas son muy raras, mínimo 1%
        if (redCards == 0) {
            baseRate = 0.01 * yellowFactor;
        } else {
            baseRate *= yellowFactor;
        }

        double probability = clampProbability(baseRate);
        return probabilityToOdds(probability);
    }

    /**
     * Calcula las cuotas de primer goleador.
     * Basado en la probabilidad relativa frente a otros posibles goleadores.
     */
    public BigDecimal calculateFirstScorerOdds(Player player, List<Player> allPotentialScorers) {
        // Calcular la "fuerza goleadora" de cada jugador
        double playerStrength = getGoalRate(player);
        double totalStrength = allPotentialScorers.stream()
                .mapToDouble(this::getGoalRate)
                .sum();

        // Probabilidad relativa
        double probability;
        if (totalStrength > 0) {
            probability = playerStrength / totalStrength;
        } else {
            // Si nadie tiene goles, distribuir equitativamente
            probability = 1.0 / allPotentialScorers.size();
        }

        // El primer gol es más difícil de predecir, reducir probabilidad
        probability *= 0.7;

        probability = clampProbability(probability);
        return probabilityToOdds(probability);
    }

    /**
     * Calcula las cuotas de gol + asistencia en el mismo partido.
     */
    public BigDecimal calculateGoalAndAssistOdds(Player player) {
        // Probabilidad combinada (aproximación simplificada)
        double goalProb = calculateGoalProbability(player);
        double assistProb = calculateAssistProbability(player);

        // Probabilidad de ambos eventos (no exactamente independientes, pero aproximación)
        // Reducimos un poco porque suelen ser eventos correlacionados negativamente
        double combinedProb = goalProb * assistProb * 0.8;

        combinedProb = clampProbability(combinedProb);
        return probabilityToOdds(combinedProb);
    }

    // ===== MÉTODOS DE UTILIDAD =====

    /**
     * Convierte una probabilidad en cuotas decimales con margen de la casa.
     */
    private BigDecimal probabilityToOdds(double probability) {
        if (probability <= 0) {
            probability = MIN_PROBABILITY;
        }

        // Cuota justa = 1 / probabilidad
        // Cuota con margen = Cuota justa * HOUSE_MARGIN
        double fairOdds = 1.0 / probability;
        double houseOdds = fairOdds * HOUSE_MARGIN;

        // Limitar cuotas mínimas y máximas
        houseOdds = Math.max(1.10, Math.min(50.0, houseOdds));

        return BigDecimal.valueOf(houseOdds).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Limita la probabilidad entre el mínimo y el máximo permitido.
     */
    private double clampProbability(double probability) {
        return Math.max(MIN_PROBABILITY, Math.min(MAX_PROBABILITY, probability));
    }

    /**
     * Obtiene el ratio de goles por partido de un jugador.
     */
    private double getGoalRate(Player player) {
        if (player.getMatchesPlayed() == 0) {
            return 0.0;
        }
        return (double) player.getGoals() / player.getMatchesPlayed();
    }

    /**
     * Obtiene el ratio de asistencias por partido de un jugador.
     */
    private double getAssistRate(Player player) {
        if (player.getMatchesPlayed() == 0) {
            return 0.0;
        }
        return (double) player.getAssists() / player.getMatchesPlayed();
    }

    /**
     * Extrae los nombres de los equipos del nombre del evento.
     */
    private String[] extractTeamsFromEvent(String eventName) {
        if (eventName == null || !eventName.contains(" vs ")) {
            return null;
        }
        return eventName.split(" vs ");
    }

    /**
     * Crea una nueva instancia de Outcome.
     */
    private Outcome createOutcome(Event event, String description, BigDecimal odds, String group) {
        Outcome outcome = new Outcome();
        outcome.setEvent(event);
        outcome.setDescription(description);
        outcome.setOdds(odds);
        outcome.setOutcomeGroup(group);
        outcome.setStatus(Outcome.OutcomeStatus.PENDING);
        return outcome;
    }

    /**
     * Regenera las cuotas de un jugador específico en todos los eventos pendientes.
     * Útil cuando se actualizan las estadísticas de un jugador.
     */
    public void recalculatePlayerOdds(Long playerId) {
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null) {
            logger.warn("No se encontró el jugador con ID: {}", playerId);
            return;
        }

        // Buscar outcomes pendientes de este jugador
        List<Outcome> playerOutcomes = outcomeRepository.findAll().stream()
                .filter(o -> o.getStatus() == Outcome.OutcomeStatus.PENDING)
                .filter(o -> o.getDescription().contains(player.getName()))
                .collect(Collectors.toList());

        for (Outcome outcome : playerOutcomes) {
            BigDecimal newOdds;

            if (outcome.getOutcomeGroup().equals("Goleadores") || 
                outcome.getDescription().contains("Gol de")) {
                newOdds = calculateGoalOdds(player);
            } else if (outcome.getOutcomeGroup().equals("Asistencias") || 
                       outcome.getDescription().contains("Asistencia de")) {
                newOdds = calculateAssistOdds(player);
            } else if (outcome.getOutcomeGroup().equals("Tarjetas")) {
                if (outcome.getDescription().contains("amarilla")) {
                    newOdds = calculateYellowCardOdds(player);
                } else {
                    newOdds = calculateRedCardOdds(player);
                }
            } else {
                continue;
            }

            outcome.setOdds(newOdds);
            outcomeRepository.save(outcome);
            logger.info("Cuotas actualizadas para {}: {} -> {}", 
                    player.getName(), outcome.getDescription(), newOdds);
        }
    }

    /**
     * Obtiene estadísticas resumidas de un jugador para mostrar en el frontend.
     */
    public PlayerStatsDTO getPlayerStats(Long playerId) {
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null) {
            return null;
        }

        PlayerStatsDTO stats = new PlayerStatsDTO();
        stats.setPlayerId(player.getId());
        stats.setPlayerName(player.getName());
        stats.setTeam(player.getTeam());
        stats.setMatchesPlayed(player.getMatchesPlayed());
        stats.setGoals(player.getGoals());
        stats.setAssists(player.getAssists());
        stats.setGoalRate(getGoalRate(player));
        stats.setAssistRate(getAssistRate(player));
        stats.setGoalOdds(calculateGoalOdds(player));
        stats.setAssistOdds(calculateAssistOdds(player));

        return stats;
    }

    /**
     * DTO interno para estadísticas de jugador.
     */
    public static class PlayerStatsDTO {
        private Long playerId;
        private String playerName;
        private String team;
        private int matchesPlayed;
        private int goals;
        private int assists;
        private double goalRate;
        private double assistRate;
        private BigDecimal goalOdds;
        private BigDecimal assistOdds;

        // Getters y Setters
        public Long getPlayerId() { return playerId; }
        public void setPlayerId(Long playerId) { this.playerId = playerId; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public String getTeam() { return team; }
        public void setTeam(String team) { this.team = team; }
        public int getMatchesPlayed() { return matchesPlayed; }
        public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }
        public int getGoals() { return goals; }
        public void setGoals(int goals) { this.goals = goals; }
        public int getAssists() { return assists; }
        public void setAssists(int assists) { this.assists = assists; }
        public double getGoalRate() { return goalRate; }
        public void setGoalRate(double goalRate) { this.goalRate = goalRate; }
        public double getAssistRate() { return assistRate; }
        public void setAssistRate(double assistRate) { this.assistRate = assistRate; }
        public BigDecimal getGoalOdds() { return goalOdds; }
        public void setGoalOdds(BigDecimal goalOdds) { this.goalOdds = goalOdds; }
        public BigDecimal getAssistOdds() { return assistOdds; }
        public void setAssistOdds(BigDecimal assistOdds) { this.assistOdds = assistOdds; }
    }
}
