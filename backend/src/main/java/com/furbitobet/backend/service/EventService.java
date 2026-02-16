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

    @Autowired
    private EmailService emailService;

    @Autowired
    private PlayerOddsService playerOddsService;

    public Event createEvent(String name, LocalDateTime date, boolean notifyUsers) {
        Event event = new Event();
        event.setName(name);
        event.setDate(date);
        event.setStatus(Event.EventStatus.UPCOMING);
        Event savedEvent = eventRepository.save(event);

        // Generar cuotas de jugadores automáticamente usando el nuevo servicio
        generatePlayerOdds(savedEvent);

        if (notifyUsers) {
            try {
                java.util.List<com.furbitobet.backend.model.User> allUsers = userService.getAllUsers();
                for (com.furbitobet.backend.model.User user : allUsers) {
                    if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                        emailService.sendNewEventEmail(
                                user.getEmail(),
                                user.getUsername(),
                                name,
                                date.toString().replace("T", " "));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error creating event notifications: " + e.getMessage());
            }
        }

        return savedEvent;
    }

    public Event createEvent(String name, LocalDateTime date) {
        return createEvent(name, date, false);
    }

    /**
     * Genera automáticamente las cuotas de jugadores para un evento.
     * Delega al PlayerOddsService que calcula las cuotas basándose en estadísticas.
     */
    public void generatePlayerOdds(Event event) {
        try {
            // Usar el servicio especializado de cuotas de jugadores
            playerOddsService.generateAllPlayerOdds(event);
        } catch (Exception e) {
            System.err.println("Error generating player odds: " + e.getMessage());
            // No fallar la creación del evento si hay error en las cuotas
        }
    }

    /**
     * Regenera las cuotas de jugadores para un evento existente.
     * Elimina las cuotas anteriores de goleadores/asistencias y las recalcula.
     */
    public void regeneratePlayerOdds(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Eliminar cuotas de jugadores existentes
        List<Outcome> existingOutcomes = outcomeRepository.findByEventId(eventId);
        for (Outcome outcome : existingOutcomes) {
            String group = outcome.getOutcomeGroup();
            if (group != null && (group.equals("Goleadores") || group.equals("Asistencias") ||
                    group.equals("Tarjetas") || group.equals("Primer Goleador") ||
                    group.equals("Especiales Jugador"))) {
                // Solo eliminar si no tiene apuestas pendientes
                if (outcome.getStatus() == Outcome.OutcomeStatus.PENDING) {
                    outcomeRepository.delete(outcome);
                }
            }
        }

        // Regenerar cuotas
        playerOddsService.generateAllPlayerOdds(event);
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
        // 1. Find the event
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 2. Find all bets associated with this event
        List<com.furbitobet.backend.model.Bet> bets = betRepository.findDistinctByOutcomes_Event_Id(id);

        // 3. Void bets and refund users if they were pending
        for (com.furbitobet.backend.model.Bet bet : bets) {
            if (bet.getStatus() == com.furbitobet.backend.model.Bet.BetStatus.PENDING) {
                bet.setStatus(com.furbitobet.backend.model.Bet.BetStatus.VOID);
                bet.setWinnings(bet.getAmount()); // Set winnings to original amount (refund)

                // Refund the user
                userService.updateBalance(bet.getUser().getId(), bet.getAmount());
                betRepository.save(bet);

                // Optionally send notification email
                try {
                    String subject = "Evento Cancelado - Apuesta #" + bet.getId() + " Anulada";
                    String body = "Hola " + bet.getUser().getUsername() + ",\n\n" +
                            "El evento \"" + event.getName() + "\" ha sido cancelado por el administrador.\n" +
                            "Tu apuesta #" + bet.getId() + " ha sido anulada.\n" +
                            "Se te ha devuelto el importe de " + String.format("%.2f", bet.getAmount()) + "€.\n\n" +
                            "Lamentamos las molestias.\n\n" +
                            "FurbitoBET";
                    emailService.sendSimpleMessage(bet.getUser().getEmail(), subject, body);
                } catch (Exception e) {
                    System.err
                            .println("Error sending cancellation email for bet " + bet.getId() + ": " + e.getMessage());
                }
            }
        }

        // 4. Delete the event (cascades to outcomes)
        eventRepository.deleteById(id);
    }

    @Autowired
    private BetService betService;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EventService.class);

    public void resolveEvent(Event event, int homeGoals, int awayGoals) {
        logger.info("Resolving event {}: {} - {}", event.getDescription(), homeGoals, awayGoals);

        // Save the match result in the event for future re-resolution
        event.setHomeGoals(homeGoals);
        event.setAwayGoals(awayGoals);

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
