package com.furbitobet.backend.controller;

import com.furbitobet.backend.model.User;
import com.furbitobet.backend.repository.UserRepository;
import com.furbitobet.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/rewards")
@CrossOrigin(origins = "*") // In production configure this properly
public class RewardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/spin-status")
    public ResponseEntity<?> getSpinStatus() {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        Map<String, Object> response = new HashMap<>();
        if (user.getLastSpinTime() == null) {
            response.put("canSpin", true);
            response.put("nextSpinTime", null);
        } else {
            LocalDateTime nextSpin = user.getLastSpinTime().plus(12, ChronoUnit.HOURS);
            if (LocalDateTime.now().isAfter(nextSpin)) {
                response.put("canSpin", true);
                response.put("nextSpinTime", null);
            } else {
                response.put("canSpin", false);
                response.put("nextSpinTime", nextSpin);
            }
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/spin")
    public ResponseEntity<?> spinRoulette() {
        User user = getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        // Check cooldown
        if (user.getLastSpinTime() != null) {
            LocalDateTime nextSpin = user.getLastSpinTime().plus(12, ChronoUnit.HOURS);
            if (LocalDateTime.now().isBefore(nextSpin)) {
                return ResponseEntity.badRequest().body("Cooldown active. Please wait.");
            }
        }

        // Generate reward (10 to 50)
        int rewardAmount = ThreadLocalRandom.current().nextInt(10, 51);
        BigDecimal reward = new BigDecimal(rewardAmount);

        // Update user
        user.setBalance(user.getBalance().add(reward));
        user.setLastSpinTime(LocalDateTime.now());
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("reward", rewardAmount);
        response.put("newBalance", user.getBalance());
        response.put("message", "Congratulations! You won " + rewardAmount + "€");

        return ResponseEntity.ok(response);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        }
        return null;
    }
}
