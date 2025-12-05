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
                    return outcome.getEvent().getDate().isAfter(now.plusHours(1));
                });

        if (!canCancel) {
            throw new RuntimeException("Too late to cancel");
        }

        User user = bet.getUser();
        user.setBalance(user.getBalance().add(bet.getAmount()));
        userRepository.save(user);

        bet.setStatus(Bet.BetStatus.CANCELLED);
        betRepository.save(bet);
    }

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
                    break;
                }
                if (outcome.getStatus() == Outcome.OutcomeStatus.LOST) {
                    anyLost = true;
                }
                if (outcome.getStatus() == Outcome.OutcomeStatus.WON) {
                    totalOdds = totalOdds.multiply(outcome.getOdds());
                }
                if (outcome.getStatus() == Outcome.OutcomeStatus.VOID) {
                    anyVoid = true;
                    // Void outcomes count as odds 1.0, so we don't multiply
                }
            }

            if (!allResolved) {
                continue;
            }

            if (anyLost) {
                bet.setStatus(Bet.BetStatus.LOST);
            } else {
                // All won or void
                if (anyVoid && totalOdds.compareTo(BigDecimal.ONE) == 0
                        && bet.getOutcomes().stream().allMatch(o -> o.getStatus() == Outcome.OutcomeStatus.VOID)) {
                    // All outcomes are VOID -> Refund
                    bet.setStatus(Bet.BetStatus.VOID); // Or REFUNDED if enum exists, assuming VOID or treating as WON
                                                       // with odds 1.0
                    User user = bet.getUser();
                    user.setBalance(user.getBalance().add(bet.getAmount()));
                    userRepository.save(user);
                } else {
                    bet.setStatus(Bet.BetStatus.WON);
                    BigDecimal winnings = bet.getAmount().multiply(totalOdds);
                    User user = bet.getUser();
                    user.setBalance(user.getBalance().add(winnings));
                    userRepository.save(user);
                }
            }
            betRepository.save(bet);
        }
    }
}
