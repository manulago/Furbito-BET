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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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
                        eventService.generatePlayerOdds(newEvent);
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

        Map<String, Double> team1Stats = getTeamStats(homeTeamOriginal, standings);
        Map<String, Double> team2Stats = getTeamStats(awayTeamOriginal, standings);

        // Calculate team strengths (no home advantage since field is neutral)
        double team1Strength = calculateTeamStrength(team1Stats);
        double team2Strength = calculateTeamStrength(team2Stats);

        // Calculate win probabilities using more sophisticated model
        double totalStrength = team1Strength + team2Strength;
        double team1WinProb = team1Strength / totalStrength;
        double team2WinProb = team2Strength / totalStrength;

        // Calculate draw probability based on how evenly matched teams are
        double strengthDiff = Math.abs(team1Strength - team2Strength);
        double drawProb = 0.25 * (1.0 - (strengthDiff / (team1Strength + team2Strength)));
        drawProb = Math.max(0.15, Math.min(0.35, drawProb)); // Clamp between 15-35%

        // Normalize probabilities
        double totalProb = team1WinProb + team2WinProb + drawProb;
        team1WinProb /= totalProb;
        team2WinProb /= totalProb;
        drawProb /= totalProb;

        // Apply margin (house edge) - 8% margin
        double margin = 0.92;
        double team1Odds = (1.0 / team1WinProb) * margin;
        double team2Odds = (1.0 / team2WinProb) * margin;
        double drawOdds = (1.0 / drawProb) * margin;

        // Ensure minimum odds
        team1Odds = Math.max(1.10, team1Odds);
        team2Odds = Math.max(1.10, team2Odds);
        drawOdds = Math.max(2.00, drawOdds);

        createOutcome(event, "1", team1Odds, "Ganador del Partido");
        createOutcome(event, "X", drawOdds, "Ganador del Partido");
        createOutcome(event, "2", team2Odds, "Ganador del Partido");

        // Recalculate normalized probabilities for derived markets
        double prob1 = 1.0 / team1Odds;
        double probX = 1.0 / drawOdds;
        double prob2 = 1.0 / team2Odds;
        totalProb = prob1 + probX + prob2;
        prob1 /= totalProb;
        probX /= totalProb;
        prob2 /= totalProb;

        // Double Chance
        createOutcome(event, "1X", (1.0 / (prob1 + probX)) * margin, "Doble Oportunidad");
        createOutcome(event, "X2", (1.0 / (probX + prob2)) * margin, "Doble Oportunidad");
        createOutcome(event, "12", (1.0 / (prob1 + prob2)) * margin, "Doble Oportunidad");

        // Draw No Bet
        createOutcome(event, "1 (Sin Empate)", (1.0 / (prob1 / (prob1 + prob2))) * margin, "Apuesta sin Empate");
        createOutcome(event, "2 (Sin Empate)", (1.0 / (prob2 / (prob1 + prob2))) * margin, "Apuesta sin Empate");

        // Over/Under Goals using Poisson distribution
        double team1AvgGoalsScored = team1Stats.get("gf") / Math.max(1, team1Stats.get("played"));
        double team1AvgGoalsConceded = team1Stats.get("ga") / Math.max(1, team1Stats.get("played"));
        double team2AvgGoalsScored = team2Stats.get("gf") / Math.max(1, team2Stats.get("played"));
        double team2AvgGoalsConceded = team2Stats.get("ga") / Math.max(1, team2Stats.get("played"));

        // Expected goals for this match
        double expectedGoalsTeam1 = (team1AvgGoalsScored + team2AvgGoalsConceded) / 2.0;
        double expectedGoalsTeam2 = (team2AvgGoalsScored + team1AvgGoalsConceded) / 2.0;
        double expectedTotalGoals = expectedGoalsTeam1 + expectedGoalsTeam2;

        double lastOverOdds = 0.0;
        double lastUnderOdds = 1000.0;

        // Over/Under total goals (0.5 to 9.5)
        for (double line = 0.5; line <= 9.5; line += 1.0) {
            double probOver = calculatePoissonOver(expectedTotalGoals, line);
            double probUnder = 1.0 - probOver;

            // Clamp probabilities
            probOver = Math.max(0.01, Math.min(0.99, probOver));
            probUnder = Math.max(0.01, Math.min(0.99, probUnder));

            double overOddsVal = (1.0 / probOver) * margin;
            double underOddsVal = (1.0 / probUnder) * margin;

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

        // Team-specific goals
        createTeamGoalOutcomes(event, homeTeamFormatted, expectedGoalsTeam1, "Goles - " + homeTeamFormatted);
        createTeamGoalOutcomes(event, awayTeamFormatted, expectedGoalsTeam2, "Goles - " + awayTeamFormatted);

        // Both Teams to Score (BTTS)
        // Probability both score at least 1 = P(Team1 >= 1) * P(Team2 >= 1)
        double probTeam1Scores = 1.0 - Math.exp(-expectedGoalsTeam1);
        double probTeam2Scores = 1.0 - Math.exp(-expectedGoalsTeam2);
        double bttsYesProb = probTeam1Scores * probTeam2Scores;
        bttsYesProb = Math.max(0.10, Math.min(0.90, bttsYesProb));

        double bttsYesOdds = (1.0 / bttsYesProb) * margin;
        double bttsNoOdds = (1.0 / (1.0 - bttsYesProb)) * margin;

        createOutcome(event, "Sí", bttsYesOdds, "Ambos Marcan");
        createOutcome(event, "No", bttsNoOdds, "Ambos Marcan");
    }

    /**
     * Calculate team strength based on multiple factors
     */
    private double calculateTeamStrength(Map<String, Double> stats) {
        double points = stats.get("points");
        double played = Math.max(1, stats.get("played"));
        double gf = stats.get("gf");
        double ga = stats.get("ga");

        // Points per game (weight: 40%)
        double ppg = points / played;
        double ppgScore = ppg / 3.0; // Normalize (max 3 points per game)

        // Goal difference (weight: 30%)
        double gd = (gf - ga) / played;
        double gdScore = (gd + 3.0) / 6.0; // Normalize (-3 to +3 range)
        gdScore = Math.max(0, Math.min(1, gdScore));

        // Offensive strength (weight: 15%)
        double offensiveScore = Math.min(1.0, (gf / played) / 5.0); // Normalize (5 goals/game = max)

        // Defensive strength (weight: 15%)
        double defensiveScore = 1.0 - Math.min(1.0, (ga / played) / 5.0); // Lower GA = better

        // Weighted combination
        double strength = (ppgScore * 0.40) + (gdScore * 0.30) + (offensiveScore * 0.15) + (defensiveScore * 0.15);

        // Ensure minimum strength
        return Math.max(0.1, strength);
    }

    /**
     * Calculate probability of scoring more than X goals using Poisson distribution
     */
    private double calculatePoissonOver(double lambda, double k) {
        double probUnder = 0.0;
        for (int i = 0; i <= (int) k; i++) {
            probUnder += poissonProbability(lambda, i);
        }
        return 1.0 - probUnder;
    }

    /**
     * Poisson probability mass function
     */
    private double poissonProbability(double lambda, int k) {
        return (Math.pow(lambda, k) * Math.exp(-lambda)) / factorial(k);
    }

    /**
     * Calculate factorial
     */
    private long factorial(int n) {
        if (n <= 1)
            return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
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
