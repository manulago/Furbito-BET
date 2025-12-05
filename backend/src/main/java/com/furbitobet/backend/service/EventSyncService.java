package com.furbitobet.backend.service;

import com.furbitobet.backend.model.Category;
import com.furbitobet.backend.model.Event;
import com.furbitobet.backend.repository.CategoryRepository;
import com.furbitobet.backend.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EventSyncService {

    private static final Logger logger = LoggerFactory.getLogger(EventSyncService.class);
    private static final String TARGET_TEAM = "INFORMÁTICA-1";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");

    @Autowired
    private ScraperService scraperService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private com.furbitobet.backend.repository.OutcomeRepository outcomeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventService eventService;

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void syncEvents() {
        logger.info("Starting event synchronization for team: {}", TARGET_TEAM);
        List<Map<String, Object>> matches = scraperService.getMatchResults();
        List<Map<String, String>> standings = scraperService.getLeagueStandings();

        Category category = categoryRepository.findByName("Fútbol 7")
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName("Fútbol 7");
                    return categoryRepository.save(newCategory);
                });

        for (Map<String, Object> match : matches) {
            String homeTeam = (String) match.get("homeTeam");
            String awayTeam = (String) match.get("awayTeam");
            String dateTimeStr = (String) match.get("dateTime");
            String score = (String) match.get("score");

            if (TARGET_TEAM.equals(homeTeam) || TARGET_TEAM.equals(awayTeam)) {
                // Check if match is played (has score)
                if (score != null && !score.trim().isEmpty()) {
                    // Match played. Check if we need to resolve it.
                    // Find event by description (approximate)
                    String description = (homeTeam + " vs " + awayTeam).replace("INFORMÁTICA-1", "Furbito FIC");

                    // We need to find the event. Since we don't have ID, we search by description
                    // and status UPCOMING
                    // Or just find all events with this description and check status
                    List<Event> events = eventRepository.findAll(); // Optimization: findByDescription
                    for (Event e : events) {
                        if (e.getDescription().equals(description) && e.getStatus() == Event.EventStatus.UPCOMING) {
                            // Found the event to resolve
                            try {
                                // Parse score "3-1"
                                String[] parts = score.split("-");
                                int homeGoals = Integer.parseInt(parts[0].trim());
                                int awayGoals = Integer.parseInt(parts[1].trim());

                                eventService.resolveEvent(e, homeGoals, awayGoals);
                            } catch (Exception ex) {
                                logger.error("Error parsing score for resolution: {}", score, ex);
                            }
                        }
                    }
                    continue;
                }

                if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
                    continue; // Skip matches without date
                }

                try {
                    java.time.LocalDateTime eventDate = java.time.LocalDateTime.parse(dateTimeStr, DATE_FORMATTER);

                    // Skip past matches
                    if (eventDate.isBefore(java.time.LocalDateTime.now())) {
                        continue;
                    }

                    String description = (homeTeam + " vs " + awayTeam).replace("INFORMÁTICA-1", "Furbito FIC");

                    // Check if event already exists (by description and approximate time)
                    boolean exists = eventRepository.findAll().stream()
                            .anyMatch(e -> e.getDescription().equals(description) &&
                                    e.getDeadline().isAfter(eventDate.minusHours(2)) &&
                                    e.getDeadline().isBefore(eventDate.plusHours(2)));

                    if (!exists) {
                        Event newEvent = new Event();
                        newEvent.setDescription(description);
                        newEvent.setName(description); // Set name as well
                        newEvent.setDeadline(eventDate);
                        newEvent.setDate(eventDate); // Set date as well
                        newEvent.setCategory(category);
                        newEvent.setStatus(com.furbitobet.backend.model.Event.EventStatus.UPCOMING);
                        eventRepository.save(newEvent);
                        logger.info("Created new event: {} at {}", description, eventDate);

                        // Calculate odds and create outcomes
                        // Ensure team names are formatted
                        String formattedHome = homeTeam.replace("INFORMÁTICA-1", "Furbito FIC");
                        String formattedAway = awayTeam.replace("INFORMÁTICA-1", "Furbito FIC");

                        // We need to pass ORIGINAL names for stats lookup, but FORMATTED names for
                        // outcome descriptions
                        createOutcomesForEvent(newEvent, homeTeam, awayTeam, formattedHome, formattedAway, standings);
                    }

                } catch (Exception e) {
                    logger.error("Error parsing match date: {}", dateTimeStr, e);
                }
            }
        }
        logger.info("Event synchronization completed.");
    }

    private void createOutcomesForEvent(Event event, String homeTeamOriginal, String awayTeamOriginal,
            String homeTeamFormatted, String awayTeamFormatted,
            List<Map<String, String>> standings) {

        Map<String, Double> homeStats = getTeamStats(homeTeamOriginal, standings);
        Map<String, Double> awayStats = getTeamStats(awayTeamOriginal, standings);

        double homePoints = homeStats.getOrDefault("points", 0.0);
        double awayPoints = awayStats.getOrDefault("points", 0.0);

        // 1X2 Market
        double diff = homePoints - awayPoints;
        double factor = diff * 0.05;

        double homeOdds = Math.max(1.1, 2.5 - factor);
        double awayOdds = Math.max(1.1, 2.5 + factor);
        double drawOdds = Math.max(2.0, 3.0 - (Math.abs(diff) * 0.02));

        createOutcome(event, "1", homeOdds, "Ganador del Partido");
        createOutcome(event, "X", drawOdds, "Ganador del Partido");
        createOutcome(event, "2", awayOdds, "Ganador del Partido");

        // Probabilities (normalized)
        double prob1 = 1.0 / homeOdds;
        double probX = 1.0 / drawOdds;
        double prob2 = 1.0 / awayOdds;
        double totalProb = prob1 + probX + prob2;
        prob1 /= totalProb;
        probX /= totalProb;
        prob2 /= totalProb;

        // Double Chance
        createOutcome(event, "1X", 1.0 / (prob1 + probX), "Doble Oportunidad");
        createOutcome(event, "X2", 1.0 / (probX + prob2), "Doble Oportunidad");
        createOutcome(event, "12", 1.0 / (prob1 + prob2), "Doble Oportunidad");

        // Draw No Bet
        createOutcome(event, "1 (Sin Empate)", 1.0 / (prob1 / (prob1 + prob2)), "Apuesta sin Empate");
        createOutcome(event, "2 (Sin Empate)", 1.0 / (prob2 / (prob1 + prob2)), "Apuesta sin Empate");

        // Over/Under Goals (0.5 to 9.5)
        // Avg goals per match = (GF + GA) / Played
        double homeAvgGoals = (homeStats.get("gf") + homeStats.get("ga")) / Math.max(1, homeStats.get("played"));
        double awayAvgGoals = (awayStats.get("gf") + awayStats.get("ga")) / Math.max(1, awayStats.get("played"));
        double matchAvgGoals = (homeAvgGoals + awayAvgGoals) / 2.0;

        double lastOverOdds = 0.0;
        double lastUnderOdds = 1000.0;

        // Iterate from 0.5 to 9.5
        for (double line = 0.5; line <= 9.5; line += 1.0) {

            double diffGoals = matchAvgGoals - line;
            // Sigmoid-like adjustment
            double probUnder = 1.0 / (1.0 + Math.exp(diffGoals));

            // Clamp probabilities
            probUnder = Math.max(0.01, Math.min(0.99, probUnder));
            double probOver = 1.0 - probUnder;

            double underOddsVal = 1.0 / probUnder;
            double overOddsVal = 1.0 / probOver;

            // Apply margin (e.g. 5%)
            underOddsVal *= 0.95;
            overOddsVal *= 0.95;

            // Monotonicity check
            if (overOddsVal <= lastOverOdds) {
                overOddsVal = lastOverOdds + 0.05;
            }
            lastOverOdds = overOddsVal;

            if (underOddsVal >= lastUnderOdds) {
                underOddsVal = lastUnderOdds - 0.05;
            }
            lastUnderOdds = underOddsVal;

            // Filter odds <= 1.01
            if (overOddsVal > 1.01) {
                createOutcome(event, "Más de " + line, overOddsVal, "Goles - Más de");
            }
            if (underOddsVal > 1.01) {
                createOutcome(event, "Menos de " + line, underOddsVal, "Goles - Menos de");
            }
        }

        // Team Goals (Home)
        // Avg goals for home team = GF / Played
        double homeTeamAvg = homeStats.get("gf") / Math.max(1, homeStats.get("played"));
        createTeamGoalOutcomes(event, homeTeamFormatted, homeTeamAvg, "Goles - " + homeTeamFormatted);

        // Team Goals (Away)
        double awayTeamAvg = awayStats.get("gf") / Math.max(1, awayStats.get("played"));
        createTeamGoalOutcomes(event, awayTeamFormatted, awayTeamAvg, "Goles - " + awayTeamFormatted);

        // Both Teams to Score (BTTS)
        // High GF and High GA -> High chance of BTTS
        double bttsProb = (homeAvgGoals + awayAvgGoals) / 6.0; // Rough heuristic
        bttsProb = Math.min(0.9, Math.max(0.1, bttsProb)); // Clamp 0.1 - 0.9

        double bttsYes = 1.0 / bttsProb;
        double bttsNo = 1.0 / (1.0 - bttsProb);

        createOutcome(event, "Sí", bttsYes, "Ambos Marcan");
        createOutcome(event, "No", bttsNo, "Ambos Marcan");
    }

    private void createTeamGoalOutcomes(Event event, String teamName, double avgGoals, String group) {
        double lastOverOdds = 0.0;
        double lastUnderOdds = 1000.0;

        for (double line = 0.5; line <= 6.5; line += 1.0) {
            double diffGoals = avgGoals - line;
            double probUnder = 1.0 / (1.0 + Math.exp(diffGoals));
            probUnder = Math.max(0.01, Math.min(0.99, probUnder));
            double probOver = 1.0 - probUnder;

            double underOddsVal = 1.0 / probUnder;
            double overOddsVal = 1.0 / probOver;

            underOddsVal *= 0.95;
            overOddsVal *= 0.95;

            if (overOddsVal <= lastOverOdds)
                overOddsVal = lastOverOdds + 0.05;
            lastOverOdds = overOddsVal;

            if (underOddsVal >= lastUnderOdds)
                underOddsVal = lastUnderOdds - 0.05;
            lastUnderOdds = underOddsVal;

            if (overOddsVal > 1.01)
                createOutcome(event, "Más de " + line, overOddsVal, group + " - Más de");
            if (underOddsVal > 1.01)
                createOutcome(event, "Menos de " + line, underOddsVal, group + " - Menos de");
        }
    }

    private void createOutcome(Event event, String desc, double odds, String group) {
        com.furbitobet.backend.model.Outcome outcome = new com.furbitobet.backend.model.Outcome();
        outcome.setEvent(event);
        outcome.setDescription(desc);
        outcome.setOutcomeGroup(group);
        outcome.setOdds(java.math.BigDecimal.valueOf(odds).setScale(2, java.math.RoundingMode.HALF_UP));
        outcome.setStatus(com.furbitobet.backend.model.Outcome.OutcomeStatus.PENDING);
        outcomeRepository.save(outcome);
    }

    private Map<String, Double> getTeamStats(String teamName, List<Map<String, String>> standings) {
        Map<String, Double> stats = new java.util.HashMap<>();
        stats.put("points", 0.0);
        stats.put("played", 0.0);
        stats.put("gf", 0.0);
        stats.put("ga", 0.0);

        for (Map<String, String> row : standings) {
            if (row.get("team").equalsIgnoreCase(teamName)) {
                try {
                    stats.put("points", Double.parseDouble(row.get("points")));
                    stats.put("played", Double.parseDouble(row.get("played")));
                    stats.put("gf", Double.parseDouble(row.get("gf")));
                    stats.put("ga", Double.parseDouble(row.get("ga")));
                } catch (NumberFormatException e) {
                    // Ignore
                }
                break;
            }
        }
        return stats;
    }
}
