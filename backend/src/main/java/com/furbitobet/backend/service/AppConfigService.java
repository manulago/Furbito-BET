package com.furbitobet.backend.service;

import com.furbitobet.backend.model.AppConfig;
import com.furbitobet.backend.repository.AppConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    @Autowired
    private AppConfigRepository appConfigRepository;

    public String getConfigValue(String key, String defaultValue) {
        return appConfigRepository.findByConfigKey(key)
                .map(AppConfig::getConfigValue)
                .orElse(defaultValue);
    }

    public void setConfigValue(String key, String value) {
        AppConfig config = appConfigRepository.findByConfigKey(key)
                .orElse(new AppConfig(key, value));
        config.setConfigValue(value);
        appConfigRepository.save(config);
    }

    public boolean isNewsModalEnabled() {
        String value = getConfigValue("news_modal_enabled", "true");
        return Boolean.parseBoolean(value);
    }

    public void setNewsModalEnabled(boolean enabled) {
        setConfigValue("news_modal_enabled", String.valueOf(enabled));
    }

    public boolean isChristmasThemeEnabled() {
        String value = getConfigValue("christmas_theme_enabled", "false");
        return Boolean.parseBoolean(value);
    }

    public void setChristmasThemeEnabled(boolean enabled) {
        setConfigValue("christmas_theme_enabled", String.valueOf(enabled));
    }

    // Carnival Theme Methods
    public boolean isCarnivalThemeEnabled() {
        String value = getConfigValue("carnival_theme_enabled", "false");
        return Boolean.parseBoolean(value);
    }

    public void setCarnivalThemeEnabled(boolean enabled) {
        setConfigValue("carnival_theme_enabled", String.valueOf(enabled));
    }
}
