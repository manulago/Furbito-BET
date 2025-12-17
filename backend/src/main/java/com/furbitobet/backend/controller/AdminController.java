package com.furbitobet.backend.controller;

import com.furbitobet.backend.model.Event;
import com.furbitobet.backend.model.Outcome;
import com.furbitobet.backend.model.User;
import com.furbitobet.backend.service.EventService;
import com.furbitobet.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

import com.furbitobet.backend.repository.EventRepository;
import com.furbitobet.backend.repository.OutcomeRepository;
import com.furbitobet.backend.repository.BetRepository;
import com.furbitobet.backend.repository.UserRepository;
import com.furbitobet.backend.model.Bet;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OutcomeRepository outcomeRepository;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.furbitobet.backend.service.EventSyncService eventSyncService;

    @PostMapping("/sync-events")
    public void syncEvents() {
        eventSyncService.syncEvents();
        // Since we also want to ensure player odds are generated if missing,
        // they should be handled by syncEvents logic if it detects new events.
        // However, if the user deleted an event and wants it back, syncEvents handles
        // it.
    }

    @PostMapping("/events")
    public Event createEvent(@RequestBody CreateEventRequest request) {
        return eventService.createEvent(request.getName(), request.getDate(), request.isNotifyUsers());
    }

    public static class CreateEventRequest {
        private String name;
        private java.time.LocalDateTime date;
        private boolean notifyUsers;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public java.time.LocalDateTime getDate() {
            return date;
        }

        public void setDate(java.time.LocalDateTime date) {
            this.date = date;
        }

        public boolean isNotifyUsers() {
            return notifyUsers;
        }

        public void setNotifyUsers(boolean notifyUsers) {
            this.notifyUsers = notifyUsers;
        }
    }

    @PutMapping("/events/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return eventService.updateEvent(id, event.getName(), event.getDate());
    }

    @PostMapping("/events/{id}/clone")
    public Event cloneEvent(@PathVariable Long id) {
        return eventService.cloneEvent(id);
    }

    @PutMapping("/outcomes/{id}")
    public Outcome updateOutcome(@PathVariable Long id, @RequestBody OutcomeRequest request) {
        // SECURITY: Validate odds are positive
        if (request.getOdds() == null || request.getOdds().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Odds must be positive");
        }
        return eventService.updateOutcome(id, request.getDescription(), request.getOdds(), request.getOutcomeGroup());
    }

    @PostMapping("/events/{eventId}/outcomes")
    public Outcome addOutcome(@PathVariable Long eventId, @RequestBody OutcomeRequest request) {
        // SECURITY: Validate odds are positive
        if (request.getOdds() == null || request.getOdds().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Odds must be positive");
        }

        Event event = eventRepository.findById(eventId).orElseThrow();
        Outcome outcome = new Outcome();
        outcome.setDescription(request.getDescription());
        outcome.setOdds(request.getOdds());
        outcome.setOutcomeGroup(request.getOutcomeGroup());
        outcome.setEvent(event);
        outcome.setStatus(Outcome.OutcomeStatus.PENDING);
        return outcomeRepository.save(outcome);
    }

    @DeleteMapping("/outcomes/{id}")
    public void deleteOutcome(@PathVariable Long id) {
        outcomeRepository.deleteById(id);
    }

    @PutMapping("/outcomes/{id}/status")
    @Transactional
    public void settleOutcome(@PathVariable Long id, @RequestParam Outcome.OutcomeStatus status) {
        Outcome outcome = outcomeRepository.findById(id).orElseThrow();
        outcome.setStatus(status);
        outcomeRepository.save(outcome);

        // Re-evaluate all bets containing this outcome
        java.util.List<Bet> allBets = betRepository.findAll();

        for (Bet bet : allBets) {
            // Check if this bet contains the modified outcome
            boolean containsOutcome = bet.getOutcomes().stream()
                    .anyMatch(o -> o.getId().equals(id));

            if (!containsOutcome) {
                continue;
            }

            // Store old bet status to handle balance adjustments
            Bet.BetStatus oldBetStatus = bet.getStatus();
            BigDecimal oldWinnings = bet.getWinnings() != null ? bet.getWinnings() : BigDecimal.ZERO;

            // Re-evaluate bet status
            boolean allResolved = true;
            boolean anyLost = false;
            BigDecimal totalOdds = BigDecimal.ONE;
            boolean anyVoid = false;

            for (Outcome o : bet.getOutcomes()) {
                if (o.getStatus() == Outcome.OutcomeStatus.PENDING) {
                    allResolved = false;
                }
                if (o.getStatus() == Outcome.OutcomeStatus.LOST) {
                    anyLost = true;
                }
                if (o.getStatus() == Outcome.OutcomeStatus.WON) {
                    totalOdds = totalOdds.multiply(o.getOdds());
                }
                if (o.getStatus() == Outcome.OutcomeStatus.VOID) {
                    anyVoid = true;
                }
            }

            User user = bet.getUser();

            // Revert previous winnings if bet was already settled
            if (oldBetStatus == Bet.BetStatus.WON && oldWinnings.compareTo(BigDecimal.ZERO) > 0) {
                user.setBalance(user.getBalance().subtract(oldWinnings));
            } else if (oldBetStatus == Bet.BetStatus.VOID && bet.getAmount() != null) {
                // Revert refund if bet was voided
                user.setBalance(user.getBalance().subtract(bet.getAmount()));
            }

            // Apply new bet status
            if (anyLost) {
                bet.setStatus(Bet.BetStatus.LOST);
                bet.setWinnings(BigDecimal.ZERO);
            } else if (!allResolved) {
                bet.setStatus(Bet.BetStatus.PENDING);
                bet.setWinnings(null);
            } else {
                // All resolved and none lost
                if (anyVoid && totalOdds.compareTo(BigDecimal.ONE) == 0
                        && bet.getOutcomes().stream().allMatch(o -> o.getStatus() == Outcome.OutcomeStatus.VOID)) {
                    // All void - refund
                    bet.setStatus(Bet.BetStatus.VOID);
                    bet.setWinnings(bet.getAmount());
                    user.setBalance(user.getBalance().add(bet.getAmount()));
                } else {
                    // Won
                    bet.setStatus(Bet.BetStatus.WON);
                    BigDecimal winnings = bet.getAmount().multiply(totalOdds);
                    bet.setWinnings(winnings);
                    user.setBalance(user.getBalance().add(winnings));
                }
            }

            userRepository.save(user);
            betRepository.save(bet);
        }
    }

    public static class OutcomeRequest {
        private String description;
        private java.math.BigDecimal odds;
        private String outcomeGroup;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public java.math.BigDecimal getOdds() {
            return odds;
        }

        public void setOdds(java.math.BigDecimal odds) {
            this.odds = odds;
        }

        public String getOutcomeGroup() {
            return outcomeGroup;
        }

        public void setOutcomeGroup(String outcomeGroup) {
            this.outcomeGroup = outcomeGroup;
        }
    }

    @PostMapping("/events/{id}/resolve")
    public void resolveEvent(@PathVariable Long id, @RequestBody ResolveRequest request) {
        Event event = eventRepository.findById(id).orElseThrow();
        eventService.resolveEvent(event, request.getHomeGoals(), request.getAwayGoals());
    }

    public static class ResolveRequest {
        private int homeGoals;
        private int awayGoals;

        public int getHomeGoals() {
            return homeGoals;
        }

        public void setHomeGoals(int homeGoals) {
            this.homeGoals = homeGoals;
        }

        public int getAwayGoals() {
            return awayGoals;
        }

        public void setAwayGoals(int awayGoals) {
            this.awayGoals = awayGoals;
        }
    }

    @Autowired
    private com.furbitobet.backend.service.EmailService emailService;

    @Autowired
    private com.furbitobet.backend.service.AppConfigService appConfigService;

    @GetMapping("/news-modal-status")
    public org.springframework.http.ResponseEntity<?> getNewsModalStatus() {
        boolean enabled = appConfigService.isNewsModalEnabled();
        return org.springframework.http.ResponseEntity.ok(
                java.util.Map.of("enabled", enabled));
    }

    @PostMapping("/disable-news-modal")
    public org.springframework.http.ResponseEntity<?> disableNewsModal() {
        appConfigService.setNewsModalEnabled(false);
        System.out.println("📢 News modal has been disabled globally by admin");
        return org.springframework.http.ResponseEntity.ok(
                java.util.Map.of(
                        "success", true,
                        "message", "News modal disabled successfully"));
    }

    @PostMapping("/enable-news-modal")
    public org.springframework.http.ResponseEntity<?> enableNewsModal() {
        appConfigService.setNewsModalEnabled(true);
        System.out.println("📢 News modal has been enabled globally by admin");
        return org.springframework.http.ResponseEntity.ok(
                java.util.Map.of(
                        "success", true,
                        "message", "News modal enabled successfully"));
    }

    @PostMapping("/send-news-email")
    public org.springframework.http.ResponseEntity<?> sendNewsEmail() {
        try {
            System.out.println("📧 Starting news email send to all users...");

            java.util.List<User> allUsers = userService.getAllUsers();
            int successCount = 0;
            int failCount = 0;

            for (User user : allUsers) {
                // Skip admin users
                if (user.getRole() == User.Role.ADMIN) {
                    continue;
                }

                try {
                    emailService.sendNewsEmail(user.getEmail(), user.getUsername());
                    successCount++;
                    System.out.println("✅ News email sent to: " + user.getEmail());
                } catch (Exception e) {
                    failCount++;
                    System.err.println("❌ Failed to send news email to " + user.getEmail() + ": " + e.getMessage());
                }

                // Small delay to avoid overwhelming the email service
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }

            String responseMessage = String.format(
                    "News emails sent! Success: %d, Failed: %d, Total: %d",
                    successCount, failCount, successCount + failCount);

            System.out.println("📊 " + responseMessage);
            return org.springframework.http.ResponseEntity.ok(responseMessage);

        } catch (Exception e) {
            System.err.println("❌ Error sending news emails: " + e.getMessage());
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError()
                    .body("Error sending news emails: " + e.getMessage());
        }
    }

    @PostMapping("/test-email")
    public org.springframework.http.ResponseEntity<?> testEmail(@RequestParam String to) {
        try {
            // SECURITY: Validate email format to prevent abuse
            if (to == null || !to.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return org.springframework.http.ResponseEntity.badRequest()
                        .body("Invalid email format");
            }

            // SECURITY: Don't log full email addresses
            System.out.println("Received test-email request for: " + to.replaceAll("(?<=.{2}).(?=.*@)", "*"));
            emailService.sendSimpleMessage(to, "FurbitoBET Test Email",
                    "This is a test email from FurbitoBET admin panel.\n\nIf you receive this, email sending is working correctly!");
            return org.springframework.http.ResponseEntity.ok("Email request queued successfully");
        } catch (Exception e) {
            System.err.println("Error in test-email endpoint: " + e.getMessage());
            // SECURITY: Don't expose stack trace in production
            // e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError()
                    .body("Error sending email");
        }
    }

    @PutMapping("/users/{id}/balance")
    public User updateBalance(@PathVariable Long id, @RequestBody BigDecimal amount) {
        return userService.updateBalance(id, amount);
    }

    @PostMapping("/send-newsletter")
    public org.springframework.http.ResponseEntity<?> sendNewsletterToAllUsers(
            @RequestBody(required = false) NewsletterRequest request) {
        try {
            System.out.println("📧 Starting newsletter send to all users...");

            java.util.List<User> allUsers = userService.getAllUsers();
            int successCount = 0;
            int failCount = 0;

            // Use custom subject and message if provided, otherwise use defaults
            String subject = (request != null && request.getSubject() != null)
                    ? request.getSubject()
                    : "🎉 ¡Novedades en FurbitoBET!";

            String message = (request != null && request.getMessage() != null)
                    ? request.getMessage()
                    : buildNewsletterMessage();

            for (User user : allUsers) {
                // Skip admin users
                if (user.getRole() == User.Role.ADMIN) {
                    continue;
                }

                try {
                    emailService.sendSimpleMessage(user.getEmail(), subject, message);
                    successCount++;
                    System.out.println("✅ Email sent to: " + user.getEmail());
                } catch (Exception e) {
                    failCount++;
                    System.err.println("❌ Failed to send email to " + user.getEmail() + ": " + e.getMessage());
                }

                // Small delay to avoid overwhelming the email service
                try {
                    Thread.sleep(100); // 100ms delay between emails
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }

            String responseMessage = String.format(
                    "Newsletter sent! Success: %d, Failed: %d, Total: %d",
                    successCount, failCount, successCount + failCount);

            System.out.println("📊 " + responseMessage);
            return org.springframework.http.ResponseEntity.ok(responseMessage);

        } catch (Exception e) {
            System.err.println("❌ Error sending newsletter: " + e.getMessage());
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError()
                    .body("Error sending newsletter: " + e.getMessage());
        }
    }

    public static class NewsletterRequest {
        private String subject;
        private String message;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private String buildNewsletterMessage() {
        return "¡Hola!\n\n" +
                "Tenemos grandes novedades en FurbitoBET que queremos compartir contigo:\n\n" +
                "📱 ¡INSTALA LA APP!\n" +
                "Ahora puedes instalar FurbitoBET en tu móvil o PC como una aplicación.\n" +
                "Acceso rápido desde tu pantalla de inicio, sin abrir el navegador.\n\n" +
                "🔹 En Android: Busca el botón \"Instalar App\" en la página\n" +
                "🔹 En iPhone: Toca Compartir → \"Añadir a pantalla de inicio\"\n\n" +
                "📱 MEJORA MÓVIL\n" +
                "Experiencia 100% optimizada para tu teléfono.\n" +
                "Navegación más fluida y accesible.\n\n" +
                "❓ NUEVA PÁGINA DE AYUDA\n" +
                "¿Dudas? Visita nuestra sección de ayuda para aprender cómo funciona todo.\n\n" +
                "⚙️ GESTIÓN DE PERFIL\n" +
                "Control total sobre tu cuenta.\n" +
                "Actualiza tus datos y preferencias fácilmente.\n\n" +
                "👀 ESPÍA A LOS MEJORES\n" +
                "Visita el perfil de otros usuarios desde el ranking.\n" +
                "Ve su historial de apuestas y estrategias.\n\n" +
                "---\n\n" +
                "¡Entra ahora y descubre todas las mejoras!\n" +
                "https://furbitobet.vercel.app\n\n" +
                "Saludos,\n" +
                "El equipo de FurbitoBET 🎰";
    }

    @PostMapping("/send-christmas-email")
    public org.springframework.http.ResponseEntity<?> sendChristmasEmail() {
        try {
            System.out.println("🎄 Starting Christmas email send to all users...");

            java.util.List<User> allUsers = userService.getAllUsers();
            int successCount = 0;
            int failCount = 0;

            for (User user : allUsers) {
                // Skip admin users
                if (user.getRole() == User.Role.ADMIN) {
                    continue;
                }

                try {
                    emailService.sendChristmasGiftEmail(user.getEmail(), user.getUsername());
                    successCount++;
                    System.out.println("✅ Christmas email sent to: " + user.getEmail());
                } catch (Exception e) {
                    failCount++;
                    System.err
                            .println("❌ Failed to send Christmas email to " + user.getEmail() + ": " + e.getMessage());
                }

                // Small delay to avoid overwhelming the email service
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }

            String responseMessage = String.format(
                    "Christmas emails sent! Success: %d, Failed: %d, Total: %d",
                    successCount, failCount, successCount + failCount);

            System.out.println("📊 " + responseMessage);
            return org.springframework.http.ResponseEntity.ok(responseMessage);

        } catch (Exception e) {
            System.err.println("❌ Error sending Christmas emails: " + e.getMessage());
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError()
                    .body("Error sending Christmas emails: " + e.getMessage());
        }
    }

    @PostMapping("/add-balance-to-all")
    @Transactional
    public org.springframework.http.ResponseEntity<?> addBalanceToAllUsers(@RequestBody AddBalanceRequest request) {
        try {
            // SECURITY: Validate amount is positive
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return org.springframework.http.ResponseEntity.badRequest()
                        .body("Amount must be positive");
            }

            // SECURITY: Limit maximum amount to prevent abuse
            if (request.getAmount().compareTo(new BigDecimal("10000")) > 0) {
                return org.springframework.http.ResponseEntity.badRequest()
                        .body("Amount too large. Maximum is 10,000€");
            }

            System.out.println("💰 Adding " + request.getAmount() + "€ to all users...");

            java.util.List<User> allUsers = userService.getAllUsers();
            int updatedCount = 0;

            for (User user : allUsers) {
                // Skip admin users
                if (user.getRole() == User.Role.ADMIN) {
                    continue;
                }

                BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                user.setBalance(currentBalance.add(request.getAmount()));
                userRepository.save(user);
                updatedCount++;
                System.out.println("✅ Added " + request.getAmount() + "€ to " + user.getUsername() +
                        " (new balance: " + user.getBalance() + "€)");
            }

            String responseMessage = String.format(
                    "Successfully added %s€ to %d users",
                    request.getAmount(), updatedCount);

            System.out.println("📊 " + responseMessage);
            return org.springframework.http.ResponseEntity.ok(
                    java.util.Map.of(
                            "success", true,
                            "message", responseMessage,
                            "usersUpdated", updatedCount,
                            "amountAdded", request.getAmount()));

        } catch (Exception e) {
            System.err.println("❌ Error adding balance to users: " + e.getMessage());
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError()
                    .body("Error adding balance: " + e.getMessage());
        }
    }

    public static class AddBalanceRequest {
        private BigDecimal amount;

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}
