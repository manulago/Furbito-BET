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

    @PostMapping
    public Bet placeBet(@RequestBody PlaceBetRequest request) {
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
    public java.util.List<Bet> getUserBets(@PathVariable Long userId) {
        return betService.getBetsByUserId(userId);
    }

    @PostMapping("/{id}/cancel")
    public void cancelBet(@PathVariable Long id, @RequestParam Long userId) {
        betService.cancelBet(id, userId);
    }
}
