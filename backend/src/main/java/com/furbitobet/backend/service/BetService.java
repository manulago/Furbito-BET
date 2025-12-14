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
        User user = userRepository.findById(userId).orElseThrow();
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

        // Check for mutually exclusive outcomes
        for (int i = 0; i < outcomes.size(); i++) {
            for (int j = i + 1; j < outcomes.size(); j++) {
                Outcome o1 = outcomes.get(i);
                Outcome o2 = outcomes.get(j);
                if (o1.getEvent().getId().equals(o2.getEvent().getId()) &&
                        o1.getOutcomeGroup() != null &&
                        o1.getOutcomeGroup().equals(o2.getOutcomeGroup())) {
                    throw new RuntimeException("No se pueden apostar a dos sucesos del mismo tipo");
                }
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
        return betRepository.save(bet);
    }

    public java.util.List<Bet> getBetsByUserId(Long userId) {
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
}
