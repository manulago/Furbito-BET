package com.furbitobet.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "app_config")
public class AppConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String configKey;

    @Column(nullable = false)
    private String configValue;

    public AppConfig() {
    }

    public AppConfig(String configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }
}
