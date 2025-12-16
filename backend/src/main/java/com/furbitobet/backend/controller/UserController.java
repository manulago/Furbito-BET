package com.furbitobet.backend.controller;

import com.furbitobet.backend.model.User;
import com.furbitobet.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private com.furbitobet.backend.repository.UserRepository userRepository;

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/balance")
    public User updateBalance(@PathVariable Long id, @RequestBody java.math.BigDecimal amount) {
        // SECURITY: Validate that balance is not negative
        if (amount.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Balance cannot be negative");
        }

        User user = userService.getUserById(id);
        user.setBalance(amount);
        return userRepository.save(user);
    }

    @PutMapping("/{id}/password")
    public User resetPassword(@PathVariable Long id, @RequestBody String newPassword) {
        User user = userService.getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @GetMapping("/ranking")
    public List<User> getRanking() {
        return userService.getRanking();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{id}/request-update")
    public void requestUpdate(@PathVariable Long id, @RequestBody ProfileUpdateRequest request) {
        userService.requestProfileUpdate(id, request.getUsername(), request.getEmail(), request.getPassword());
    }

    @PostMapping("/confirm-update")
    public void confirmUpdate(@RequestBody java.util.Map<String, String> body) {
        String token = body.get("token");
        userService.confirmProfileUpdate(token);
    }

    public static class ProfileUpdateRequest {
        private String username;
        private String email;
        private String password;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
