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
            String adminPassword = System.getenv("ADMIN_PASSWORD");
            java.util.Optional<User> adminUser = userService.findByUsername("admin");

            if (adminUser.isEmpty()) {
                if (adminPassword == null || adminPassword.isEmpty()) {
                    adminPassword = java.util.UUID.randomUUID().toString();
                    System.out.println("GENERATED ADMIN PASSWORD: " + adminPassword);
                }
                userService.createUser("admin", adminPassword, "admin@furbitobet.com", User.Role.ADMIN);
            } else if (adminPassword != null && !adminPassword.isEmpty()) {
                // Update password if env var is set
                userService.updatePassword(adminUser.get().getId(), adminPassword);
                System.out.println("ADMIN PASSWORD UPDATED FROM ENV VAR");
            }
            if (userService.findByUsername("user").isEmpty()) {
                userService.createUser("user", "user", "user@furbitobet.com", User.Role.USER);
            }
        };
    }
}
