package com.furbitobet.backend.config;

import com.furbitobet.backend.model.User;
import com.furbitobet.backend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserService userService) {
        return args -> {
            System.out.println("STARTING DATA INITIALIZATION...");
            String adminPassword = System.getenv("ADMIN_PASSWORD");
            System.out.println("ADMIN_PASSWORD env var is " + (adminPassword != null ? "SET" : "NOT SET"));

            java.util.Optional<User> adminUser = userService.findByUsername("admin");

            if (adminUser.isEmpty()) {
                System.out.println("Admin user not found. Creating new admin user...");
                if (adminPassword == null || adminPassword.isEmpty()) {
                    adminPassword = java.util.UUID.randomUUID().toString();
                    System.out.println("GENERATED ADMIN PASSWORD: " + adminPassword);
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

            // Default user creation disabled by request
            // if (userService.findByUsername("user").isEmpty()) {
            // userService.createUser("user", "user", "user@furbitobet.com",
            // User.Role.USER);
            // }
            System.out.println("DATA INITIALIZATION COMPLETE.");
        };
    }
}
