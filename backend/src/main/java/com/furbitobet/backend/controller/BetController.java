package com.furbitobet.backend.controller;

import com.furbitobet.backend.model.Bet;
import com.furbitobet.backend.service.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bets")
@CrossOrigin(origins = "*")
public class BetController {
    @Autowired
    private BetService betService;

    @Autowired
    private com.furbitobet.backend.service.UserService userService;

    @PostMapping
    public Bet placeBet(@RequestBody PlaceBetRequest request,
            org.springframework.security.core.Authentication authentication) {
        // SECURITY: Verify that the authenticated user matches the userId in the
        // request
        String authenticatedUsername = authentication.getName();
        com.furbitobet.backend.model.User requestUser = userService.getUserById(request.getUserId());

        if (!requestUser.getUsername().equals(authenticatedUsername)) {
            throw new RuntimeException("Unauthorized: Cannot place bets for other users");
        }

        return betService.placeBet(request.getUserId(), request.getOutcomeIds(), request.getAmount());
    }

    public static class PlaceBetRequest {
        private Long userId;
        private java.util.List<Long> outcomeIds;
        private BigDecimal amount;

        // Getters and Setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public java.util.List<Long> getOutcomeIds() {
            return outcomeIds;
        }

        public void setOutcomeIds(java.util.List<Long> outcomeIds) {
            this.outcomeIds = outcomeIds;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    @GetMapping("/user/{userId}")
    public java.util.List<Bet> getUserBets(@PathVariable Long userId,
            org.springframework.security.core.Authentication authentication) {
        // SECURITY: Verify that the authenticated user can only view their own bets
        String authenticatedUsername = authentication.getName();
        com.furbitobet.backend.model.User requestUser = userService.getUserById(userId);

        if (!requestUser.getUsername().equals(authenticatedUsername)) {
            throw new RuntimeException("Unauthorized: Cannot view other users' bets");
        }

        return betService.getBetsByUserId(userId);
    }

    @PostMapping("/{id}/cancel")
    public void cancelBet(@PathVariable Long id, @RequestParam Long userId) {
        betService.cancelBet(id, userId);
    }
}
