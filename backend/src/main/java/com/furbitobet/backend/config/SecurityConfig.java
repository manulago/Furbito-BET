package com.furbitobet.backend.config;

import com.furbitobet.backend.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/forgot-password",
                                "/api/auth/reset-password")
                        .permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/events/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/events/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/events/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/events/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/league/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/ranking").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/players/**").permitAll()

                        // User Profile Management (specific paths before general ones)
                        .requestMatchers("/api/users/confirm-update").permitAll()
                        .requestMatchers("/api/users/*/request-update").authenticated()

                        // SECURITY FIX: Admin-only endpoints (must be before general /api/users/**)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/players/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/players/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/players/**").hasRole("ADMIN")

                        // SECURITY FIX: All /api/users endpoints require ADMIN role (except specific
                        // ones above)
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // Authenticated endpoints
                        .requestMatchers("/api/bets/**").authenticated()

                        .anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // Allow all origins explicitly for development/testing
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
