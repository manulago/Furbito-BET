package com.furbitobet.backend.config;

import com.furbitobet.backend.model.User;
import com.furbitobet.backend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserService userService,
            com.furbitobet.backend.repository.PlayerRepository playerRepository) {
        return args -> {
            System.out.println("STARTING DATA INITIALIZATION...");
            String adminPassword = System.getenv("ADMIN_PASSWORD");
            System.out.println("ADMIN_PASSWORD env var is " + (adminPassword != null ? "SET" : "NOT SET"));

            java.util.Optional<User> adminUser = userService.findByUsername("admin");

            if (adminUser.isEmpty()) {
                System.out.println("Admin user not found. Creating new admin user...");
                if (adminPassword == null || adminPassword.isEmpty()) {
                    adminPassword = java.util.UUID.randomUUID().toString();
                    // SECURITY: Never log passwords, even generated ones
                    System.out.println("GENERATED ADMIN PASSWORD: ********** (check environment or regenerate)");
                }
                userService.createUser("admin", adminPassword, "admin@furbitobet.com", User.Role.ADMIN);
                System.out.println("Admin user created successfully.");
            } else if (adminPassword != null && !adminPassword.isEmpty()) {
                // Update password if env var is set
                System.out.println("Admin user found. Updating password from env var...");
                userService.updatePassword(adminUser.get().getId(), adminPassword);
                System.out.println("ADMIN PASSWORD UPDATED FROM ENV VAR");
            } else {
                System.out.println("Admin user found. No password update required (env var not set).");
            }

            if (playerRepository.count() == 0) {
                System.out.println("No players found. Adding dummy players for Furbito FIC...");
                String teamName = "Furbito FIC";

                com.furbitobet.backend.model.Player p1 = new com.furbitobet.backend.model.Player();
                p1.setName("Manuel Lago");
                p1.setTeam(teamName);
                p1.setGoals(10);
                p1.setAssists(5);
                p1.setMatchesPlayed(8);
                playerRepository.save(p1);

                com.furbitobet.backend.model.Player p2 = new com.furbitobet.backend.model.Player();
                p2.setName("Juan Pérez");
                p2.setTeam(teamName);
                p2.setGoals(2);
                p2.setAssists(8);
                p2.setMatchesPlayed(8);
                playerRepository.save(p2);

                com.furbitobet.backend.model.Player p3 = new com.furbitobet.backend.model.Player();
                p3.setName("Carlos Lopez");
                p3.setTeam(teamName);
                p3.setGoals(5);
                p3.setAssists(2);
                p3.setMatchesPlayed(7);
                playerRepository.save(p3);

                System.out.println("Dummy players added.");
            }

            System.out.println("DATA INITIALIZATION COMPLETE.");

        };
    }
}
