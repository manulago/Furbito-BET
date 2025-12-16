package com.furbitobet.backend.service;

import com.furbitobet.backend.model.Bet;
import com.furbitobet.backend.model.Outcome;
import com.furbitobet.backend.model.User;
import com.furbitobet.backend.repository.BetRepository;
import com.furbitobet.backend.repository.OutcomeRepository;
import com.furbitobet.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class BetService {
    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OutcomeRepository outcomeRepository;

    @Transactional
    public Bet placeBet(Long userId, java.util.List<Long> outcomeIds, BigDecimal amount) {
        if (userId == null)
            throw new IllegalArgumentException("User ID cannot be null");
        if (outcomeIds == null)
            throw new IllegalArgumentException("Outcome IDs cannot be null");
        if (amount == null)
            throw new IllegalArgumentException("Amount cannot be null");

        // SECURITY: Prevent negative or zero bet amounts
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bet amount must be positive");
        }

        // SECURITY: Use pessimistic lock to prevent race condition
        // Without this, two simultaneous bets could both check balance=100,
        // then both subtract 100, resulting in balance=-100
        User user = userRepository.findByIdWithLock(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        java.util.List<Outcome> outcomes = outcomeRepository.findAllById(outcomeIds);

        if (outcomes.isEmpty()) {
            throw new RuntimeException("No outcomes selected");
        }

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // Check if any event is completed, cancelled, or ALREADY STARTED
        for (Outcome outcome : outcomes) {
            if (outcome.getEvent().getStatus() == com.furbitobet.backend.model.Event.EventStatus.COMPLETED ||
                    outcome.getEvent().getStatus() == com.furbitobet.backend.model.Event.EventStatus.CANCELLED) {
                throw new RuntimeException(
                        "Cannot place bet on completed or cancelled event: " + outcome.getEvent().getName());
            }
            if (outcome.getEvent().getDate().isBefore(now)) {
                throw new RuntimeException(
                        "Cannot place bet on started event: " + outcome.getEvent().getName());
            }
        }

        // Check for mutually exclusive or overlapping outcomes
        for (int i = 0; i < outcomes.size(); i++) {
            for (int j = i + 1; j < outcomes.size(); j++) {
                Outcome o1 = outcomes.get(i);
                Outcome o2 = outcomes.get(j);

                // Skip if different events
                if (!o1.getEvent().getId().equals(o2.getEvent().getId())) {
                    continue;
                }

                // Both outcomes are from the same event
                String group1 = o1.getOutcomeGroup();
                String group2 = o2.getOutcomeGroup();

                if (group1 == null || group2 == null) {
                    continue;
                }

                // Extract base category and direction
                String baseCategory1 = extractBaseCategory(group1);
                String baseCategory2 = extractBaseCategory(group2);

                // If different base categories, they're compatible
                if (!baseCategory1.equals(baseCategory2)) {
                    continue;
                }

                // Allow combining multiple "Goleadores" or "Asistencias" (player-specific bets)
                if (baseCategory1.equals("Goleadores") || baseCategory1.equals("Asistencias")) {
                    continue;
                }

                // Same base category - check if they overlap
                String direction1 = extractDirection(group1);
                String direction2 = extractDirection(group2);

                // If both are in the same direction (both "Más de" or both "Menos de"), they
                // overlap
                if (direction1.equals(direction2) && !direction1.isEmpty()) {
                    throw new RuntimeException(
                            "No se pueden combinar apuestas del mismo tipo y dirección para el mismo evento. " +
                                    "Por ejemplo, no puedes apostar a 'Menos de 5.5' y 'Menos de 4.5' al mismo tiempo.");
                }

                // If both have no direction (like "Ganador del Partido"), they're mutually
                // exclusive
                if (direction1.isEmpty() && direction2.isEmpty()) {
                    throw new RuntimeException(
                            "No se pueden combinar dos apuestas del mismo tipo para el mismo evento.");
                }

                // Different directions (one "Más de", one "Menos de") are allowed
            }
        }

        if (user.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);

        Bet bet = new Bet();
        bet.setUser(user);
        bet.setOutcomes(outcomes);
        bet.setAmount(amount);
        bet.setStatus(Bet.BetStatus.PENDING);
        bet.setPlacedAt(now);
        return betRepository.save(bet);
    }

    /**
     * Extracts the base category from an outcomeGroup string.
     * For example:
     * - "Goles - Furbito FIC - Más de" -> "Goles - Furbito FIC"
     * - "Goles - ECONÓMICAS-5 - Menos de" -> "Goles - ECONÓMICAS-5"
     * - "Ganador del Partido" -> "Ganador del Partido"
     * - "Ambos Marcan" -> "Ambos Marcan"
     */
    private String extractBaseCategory(String outcomeGroup) {
        if (outcomeGroup == null) {
            return "";
        }

        // Remove common directional indicators
        String base = outcomeGroup
                .replaceAll(" - Más de$", "")
                .replaceAll(" - Menos de$", "")
                .replaceAll(" - Over$", "")
                .replaceAll(" - Under$", "")
                .trim();

        return base;
    }

    /**
     * Extracts the direction from an outcomeGroup string.
     * Returns "MAS" for "Más de", "MENOS" for "Menos de", or "" for no direction.
     */
    private String extractDirection(String outcomeGroup) {
        if (outcomeGroup == null) {
            return "";
        }

        if (outcomeGroup.contains(" - Más de") || outcomeGroup.contains(" - Over")) {
            return "MAS";
        }
        if (outcomeGroup.contains(" - Menos de") || outcomeGroup.contains(" - Under")) {
            return "MENOS";
        }

        return "";
    }

    public java.util.List<Bet> getBetsByUserId(Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("User ID cannot be null");
        return betRepository.findByUserId(userId);
    }

    @Transactional
    public void cancelBet(Long betId, Long userId) {
        Bet bet = betRepository.findById(betId).orElseThrow(() -> new RuntimeException("Bet not found"));

        if (!bet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (bet.getStatus() != Bet.BetStatus.PENDING) {
            throw new RuntimeException("Bet cannot be cancelled");
        }

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        boolean canCancel = bet.getOutcomes().stream()
                .allMatch(outcome -> {
                    if (outcome.getEvent() == null) {
                        return false;
                    }
                    // Allow cancel until the event starts (was plusHours(1))
                    return outcome.getEvent().getDate().isAfter(now);
                });

        if (!canCancel) {
            throw new RuntimeException("Too late to cancel (event started)");
        }

        User user = bet.getUser();
        user.setBalance(user.getBalance().add(bet.getAmount()));
        userRepository.save(user);

        bet.setStatus(Bet.BetStatus.CANCELLED);
        betRepository.save(bet);
    }

    @Autowired
    private EmailService emailService;

    @Transactional
    public void settleBets(com.furbitobet.backend.model.Event event) {
        java.util.List<Bet> pendingBets = betRepository.findByStatus(Bet.BetStatus.PENDING);

        for (Bet bet : pendingBets) {
            boolean allResolved = true;
            boolean anyLost = false;
            BigDecimal totalOdds = BigDecimal.ONE;
            boolean anyVoid = false;

            for (Outcome outcome : bet.getOutcomes()) {
                if (outcome.getStatus() == Outcome.OutcomeStatus.PENDING) {
                    allResolved = false;
                    // Don't break, we need to check if any other outcome is LOST
                }
                if (outcome.getStatus() == Outcome.OutcomeStatus.LOST) {
                    anyLost = true;
                }
                if (outcome.getStatus() == Outcome.OutcomeStatus.WON) {
                    totalOdds = totalOdds.multiply(outcome.getOdds());
                }
                if (outcome.getStatus() == Outcome.OutcomeStatus.VOID) {
                    anyVoid = true;
                }
            }

            // If any outcome is lost, the bet is lost, regardless of other pending outcomes
            if (!anyLost && !allResolved) {
                continue;
            }

            String subject = "Resultado de tu apuesta #" + bet.getId();
            String body = "";

            if (anyLost) {
                bet.setStatus(Bet.BetStatus.LOST);
                bet.setWinnings(BigDecimal.ZERO);
                body = "Hola " + bet.getUser().getUsername() + ",\n\n" +
                        "Tu apuesta #" + bet.getId() + " ha sido resuelta como PERDIDA.\n" +
                        "Más suerte la próxima vez.\n\n" +
                        "FurbitoBET";

            } else {
                // All won or void
                if (anyVoid && totalOdds.compareTo(BigDecimal.ONE) == 0
                        && bet.getOutcomes().stream().allMatch(o -> o.getStatus() == Outcome.OutcomeStatus.VOID)) {
                    // All outcomes are VOID -> Refund
                    bet.setStatus(Bet.BetStatus.VOID);
                    bet.setWinnings(bet.getAmount());
                    User user = bet.getUser();
                    user.setBalance(user.getBalance().add(bet.getAmount()));
                    userRepository.save(user);

                    body = "Hola " + bet.getUser().getUsername() + ",\n\n" +
                            "Tu apuesta #" + bet.getId() + " ha sido ANULADA.\n" +
                            "Se te ha devuelto el importe de " + String.format("%.2f", bet.getAmount()) + "€.\n\n" +
                            "FurbitoBET";

                } else {
                    bet.setStatus(Bet.BetStatus.WON);
                    BigDecimal winnings = bet.getAmount().multiply(totalOdds);
                    bet.setWinnings(winnings);
                    User user = bet.getUser();
                    user.setBalance(user.getBalance().add(winnings));
                    userRepository.save(user);

                    body = "Hola " + bet.getUser().getUsername() + ",\n\n" +
                            "¡Felicidades! Tu apuesta #" + bet.getId() + " ha sido GANADORA.\n" +
                            "Has ganado " + String.format("%.2f", winnings) + "€.\n" +
                            "Tu nuevo saldo es: " + String.format("%.2f", user.getBalance()) + "€.\n\n" +
                            "FurbitoBET";

                }
            }
            betRepository.save(bet);

            // Send email asynchronously or safely catch errors to not rollback transaction
            try {
                emailService.sendSimpleMessage(bet.getUser().getEmail(), subject, body);
            } catch (Exception e) {
                System.err.println("Error sending email for bet " + bet.getId() + ": " + e.getMessage());
            }
        }
    }

    public java.util.List<com.furbitobet.backend.dto.EventResultDTO> getEventResults(Long eventId) {
        java.util.List<Bet> bets = betRepository.findDistinctByOutcomes_Event_Id(eventId);

        // Group bets by user
        java.util.Map<User, java.util.List<Bet>> betsByUser = bets.stream()
                .collect(java.util.stream.Collectors.groupingBy(Bet::getUser));

        java.util.List<com.furbitobet.backend.dto.EventResultDTO> results = new java.util.ArrayList<>();

        for (java.util.Map.Entry<User, java.util.List<Bet>> entry : betsByUser.entrySet()) {
            User user = entry.getKey();
            java.util.List<Bet> userBets = entry.getValue();

            BigDecimal totalWagered = BigDecimal.ZERO;
            BigDecimal totalWon = BigDecimal.ZERO;
            java.util.List<com.furbitobet.backend.dto.BetSummaryDTO> betSummaries = new java.util.ArrayList<>();

            for (Bet bet : userBets) {
                totalWagered = totalWagered.add(bet.getAmount());

                BigDecimal winnings = BigDecimal.ZERO;
                if (bet.getStatus() == Bet.BetStatus.WON) {
                    BigDecimal totalOdds = BigDecimal.ONE;
                    for (Outcome outcome : bet.getOutcomes()) {
                        if (outcome.getStatus() == Outcome.OutcomeStatus.WON) {
                            totalOdds = totalOdds.multiply(outcome.getOdds());
                        }
                    }
                    winnings = bet.getAmount().multiply(totalOdds);
                    totalWon = totalWon.add(winnings);
                }

                // Calculate potential winnings for display
                BigDecimal potentialOdds = BigDecimal.ONE;
                for (Outcome outcome : bet.getOutcomes()) {
                    potentialOdds = potentialOdds.multiply(outcome.getOdds());
                }
                BigDecimal potentialWinnings = bet.getAmount().multiply(potentialOdds);

                java.util.List<String> outcomeDescriptions = bet.getOutcomes().stream()
                        .map(o -> o.getEvent().getName() + ": " + o.getDescription() + " (@" + o.getOdds() + ")")
                        .collect(java.util.stream.Collectors.toList());

                betSummaries.add(new com.furbitobet.backend.dto.BetSummaryDTO(
                        bet.getId(),
                        bet.getAmount(),
                        potentialWinnings,
                        bet.getStatus().toString(),
                        outcomeDescriptions));
            }

            results.add(new com.furbitobet.backend.dto.EventResultDTO(
                    user.getUsername(),
                    totalWagered,
                    totalWon,
                    betSummaries));
        }

        // Sort by net profit (Won - Wagered) descending
        results.sort((r1, r2) -> {
            BigDecimal net1 = r1.getTotalWon().subtract(r1.getTotalWagered());
            BigDecimal net2 = r2.getTotalWon().subtract(r2.getTotalWagered());
            return net2.compareTo(net1);
        });

        return results;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
