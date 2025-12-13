package com.furbitobet.backend.service;

import com.furbitobet.backend.model.Event;
import com.furbitobet.backend.model.Outcome;
import com.furbitobet.backend.repository.EventRepository;
import com.furbitobet.backend.repository.OutcomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OutcomeRepository outcomeRepository;

    @Autowired
    private com.furbitobet.backend.repository.PlayerRepository playerRepository;

    public Event createEvent(String name, LocalDateTime date) {
        Event event = new Event();
        event.setName(name);
        event.setDate(date);
        event.setStatus(Event.EventStatus.UPCOMING);
        Event savedEvent = eventRepository.save(event);

        generatePlayerOdds(savedEvent);

        return savedEvent;
    }

    public void generatePlayerOdds(Event event) {
        try {
            String[] teams = event.getName().split(" vs ");
            if (teams.length != 2)
                return;

            java.util.List<com.furbitobet.backend.model.Player> homePlayers = playerRepository
                    .findByTeam(teams[0].trim());
            java.util.List<com.furbitobet.backend.model.Player> awayPlayers = playerRepository
                    .findByTeam(teams[1].trim());

            java.util.List<com.furbitobet.backend.model.Player> allPlayers = new java.util.ArrayList<>();
            allPlayers.addAll(homePlayers);
            allPlayers.addAll(awayPlayers);

            for (com.furbitobet.backend.model.Player p : allPlayers) {
                // Goal Odds Logic
                double matches = p.getMatchesPlayed() > 0 ? p.getMatchesPlayed() : 1.0;
                double goalProb = (double) p.getGoals() / matches;
                // Normalize prob
                goalProb = Math.max(0.1, Math.min(0.8, goalProb)); // Clamp between 10% and 80%
                // Add a margin? If user wants simple stats based.
                // Odds = 1 / Probability * Margin (e.g. 0.9 return to player) -> Odds = 0.9 /
                // Prob ???
                // Let's just do Odds = 1 / Prob for simplicity + small buffer for house edge
                // (e.g. 1.05 / Prob would be generous, usually 1/Prob * 0.9)
                // Let's assume Probability is accurate, Fair Odds = 1/P. House Odds = 1/P *
                // 0.9.
                // But typically for "will score", if players have 0 goals, odds should be high.
                if (p.getGoals() == 0)
                    goalProb = 0.1;

                BigDecimal goalOdds = BigDecimal.valueOf(1.0 / goalProb).setScale(2, java.math.RoundingMode.HALF_UP);

                addOutcome(event.getId(), "Gol de " + p.getName(), goalOdds, "Goleadores");

                // Assist Odds Logic
                double assistProb = (double) p.getAssists() / matches;
                assistProb = Math.max(0.05, Math.min(0.7, assistProb));
                if (p.getAssists() == 0)
                    assistProb = 0.08;

                BigDecimal assistOdds = BigDecimal.valueOf(1.0 / assistProb).setScale(2,
                        java.math.RoundingMode.HALF_UP);

                addOutcome(event.getId(), "Asistencia de " + p.getName(), assistOdds, "Asistencias");
            }
        } catch (Exception e) {
            System.err.println("Error generating player odds: " + e.getMessage());
            // Don't fail event creation
        }
    }

    public Outcome addOutcome(Long eventId, String description, BigDecimal odds) {
        return addOutcome(eventId, description, odds, "General");
    }

    public Outcome addOutcome(Long eventId, String description, BigDecimal odds, String group) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        Outcome outcome = new Outcome();
        outcome.setEvent(event);
        outcome.setDescription(description);
        outcome.setOdds(odds);
        outcome.setStatus(Outcome.OutcomeStatus.PENDING);
        outcome.setOutcomeGroup(group);
        return outcomeRepository.save(outcome);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public Event updateEvent(Long id, String name, LocalDateTime date) {
        Event event = getEventById(id);
        if (name != null)
            event.setName(name);
        if (date != null)
            event.setDate(date);
        return eventRepository.save(event);
    }

    @org.springframework.transaction.annotation.Transactional
    public Event cloneEvent(Long eventId) {
        Event original = getEventById(eventId);
        Event clone = new Event();
        clone.setName("Copy of " + original.getName());
        clone.setDate(original.getDate());
        clone.setStatus(Event.EventStatus.UPCOMING);
        Event savedClone = eventRepository.save(clone);

        for (Outcome o : original.getOutcomes()) {
            Outcome newOutcome = new Outcome();
            newOutcome.setDescription(o.getDescription());
            newOutcome.setOdds(o.getOdds());
            newOutcome.setOutcomeGroup(o.getOutcomeGroup());
            newOutcome.setEvent(savedClone);
            newOutcome.setStatus(Outcome.OutcomeStatus.PENDING);
            outcomeRepository.save(newOutcome);
        }
        return savedClone;
    }

    public Outcome updateOutcome(Long id, String description, BigDecimal odds, String outcomeGroup) {
        Outcome outcome = outcomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Outcome not found"));
        if (description != null)
            outcome.setDescription(description);
        if (odds != null)
            outcome.setOdds(odds);
        outcome.setOutcomeGroup(outcomeGroup);
        return outcomeRepository.save(outcome);
    }

    @Autowired
    private com.furbitobet.backend.repository.BetRepository betRepository;

    @Autowired
    private com.furbitobet.backend.service.UserService userService;

    @org.springframework.transaction.annotation.Transactional
    public void deleteEvent(Long id) {
        // 1. Find all bets associated with this event
        List<com.furbitobet.backend.model.Bet> bets = betRepository.findDistinctByOutcomes_Event_Id(id);

        // 2. Void bets and refund users if they were pending
        for (com.furbitobet.backend.model.Bet bet : bets) {
            if (bet.getStatus() == com.furbitobet.backend.model.Bet.BetStatus.PENDING) {
                bet.setStatus(com.furbitobet.backend.model.Bet.BetStatus.VOID);
                userService.updateBalance(bet.getUser().getId(), bet.getAmount());
                betRepository.save(bet);
            }
        }

        // 3. Delete the event (cascades to outcomes)
        eventRepository.deleteById(id);
    }

    @Autowired
    private BetService betService;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EventService.class);

    public void resolveEvent(Event event, int homeGoals, int awayGoals) {
        logger.info("Resolving event {}: {} - {}", event.getDescription(), homeGoals, awayGoals);

        List<Outcome> outcomes = outcomeRepository.findByEventId(event.getId());

        for (Outcome outcome : outcomes) {
            String desc = outcome.getDescription();
            String group = outcome.getOutcomeGroup();

            boolean won = false;
            boolean voided = false;

            // 1X2
            if (group.equals("Ganador del Partido")) {
                if (desc.equals("1") && homeGoals > awayGoals)
                    won = true;
                else if (desc.equals("X") && homeGoals == awayGoals)
                    won = true;
                else if (desc.equals("2") && awayGoals > homeGoals)
                    won = true;
            }
            // Double Chance
            else if (group.equals("Doble Oportunidad")) {
                if (desc.equals("1X") && homeGoals >= awayGoals)
                    won = true;
                else if (desc.equals("X2") && awayGoals >= homeGoals)
                    won = true;
                else if (desc.equals("12") && homeGoals != awayGoals)
                    won = true;
            }
            // Draw No Bet
            else if (group.equals("Apuesta sin Empate")) {
                if (homeGoals == awayGoals)
                    voided = true;
                else if (desc.startsWith("1") && homeGoals > awayGoals)
                    won = true;
                else if (desc.startsWith("2") && awayGoals > homeGoals)
                    won = true;
            }
            // Over/Under Goals
            else if (group.equals("Goles - Más de")) {
                double line = Double.parseDouble(desc.replace("Más de ", ""));
                if (homeGoals + awayGoals > line)
                    won = true;
            } else if (group.equals("Goles - Menos de")) {
                double line = Double.parseDouble(desc.replace("Menos de ", ""));
                if (homeGoals + awayGoals < line)
                    won = true;
            }
            // Team Goals
            else if (group.contains(" - Más de")) {
                double line = Double.parseDouble(desc.replace("Más de ", ""));
                if (group.contains(event.getDescription().split(" vs ")[0])) { // Home Team
                    if (homeGoals > line)
                        won = true;
                } else { // Away Team
                    if (awayGoals > line)
                        won = true;
                }
            } else if (group.contains(" - Menos de")) {
                double line = Double.parseDouble(desc.replace("Menos de ", ""));
                if (group.contains(event.getDescription().split(" vs ")[0])) { // Home Team
                    if (homeGoals < line)
                        won = true;
                } else { // Away Team
                    if (awayGoals < line)
                        won = true;
                }
            }
            // BTTS
            else if (group.equals("Ambos Marcan")) {
                if (desc.equals("Sí") && homeGoals > 0 && awayGoals > 0)
                    won = true;
                else if (desc.equals("No") && (homeGoals == 0 || awayGoals == 0))
                    won = true;
            }

            if (voided) {
                outcome.setStatus(Outcome.OutcomeStatus.VOID);
            } else if (won) {
                outcome.setStatus(Outcome.OutcomeStatus.WON);
            } else {
                outcome.setStatus(Outcome.OutcomeStatus.LOST);
            }
            outcomeRepository.save(outcome);
        }

        event.setStatus(Event.EventStatus.COMPLETED);
        eventRepository.save(event);

        betService.settleBets(event);
    }
}
