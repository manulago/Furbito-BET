package com.furbitobet.backend.controller;

import com.furbitobet.backend.model.User;
import com.furbitobet.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.furbitobet.backend.service.EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);
        user.setEnabled(false); // Account disabled until confirmed

        String token = java.util.UUID.randomUUID().toString();
        user.setConfirmationToken(token);
        user.setTokenExpiry(java.time.LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        try {
            emailService.sendAccountConfirmationEmail(request.getEmail(), token);
        } catch (Exception e) {
            // If email fails, we should probably rollback or warn.
            // For now, logging error. In production, transactional rollback might be better
            // but we'll stick to simple handling.
            System.err.println("Error sending email: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body("User registered but failed to send confirmation email. Please contact support.");
        }

        return ResponseEntity.ok("User registered successfully! Please check your email to confirm your account.");
    }

    @GetMapping("/confirm-account")
    public ResponseEntity<?> confirmAccount(@RequestParam("token") String token) {
        java.util.Optional<User> userOpt = userRepository.findByConfirmationToken(token);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        User user = userOpt.get();
        if (user.getTokenExpiry() != null && user.getTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token has expired");
        }

        user.setEnabled(true);
        user.setConfirmationToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Account confirmed successfully! You can now login.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email required");
        }

        java.util.Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = java.util.UUID.randomUUID().toString();
            user.setResetToken(token);
            userRepository.save(user);

            try {
                emailService.sendResetPasswordEmail(user.getEmail(), token);
            } catch (Exception e) {
                System.err.println("Error sending reset email: " + e.getMessage());
                return ResponseEntity.status(500).body("Error sending email");
            }
        }
        // Always return OK strictly for security (so people can't check which emails
        // exist)
        // But the user asked: "si hay algún usuario que exista cnonsense email en la
        // base
        // de datos, se le envíe un correo"
        return ResponseEntity.ok("If that email exists, a reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        java.util.Optional<User> userOpt = userRepository.findByResetToken(request.getToken());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password has been reset successfully.");
    }

    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    @Autowired
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Autowired
    private com.furbitobet.backend.security.JwtUtil jwtUtil;

    @Autowired
    private com.furbitobet.backend.service.UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: Invalid username or password");
        }

        final org.springframework.security.core.userdetails.UserDetails userDetails = userService
                .loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByUsername(request.getUsername()).get();

        return ResponseEntity.ok(new AuthResponse(jwt, user));
    }

    public static class AuthResponse {
        private String token;
        private User user;

        public AuthResponse(String token, User user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegisterRequest {
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
