package com.furbitobet.backend.controller;

import com.furbitobet.backend.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/league")
@CrossOrigin(origins = "*")
public class LeagueController {

    @Autowired
    private ScraperService scraperService;

    @GetMapping("/standings")
    public List<Map<String, String>> getStandings() {
        return scraperService.getLeagueStandings();
    }

    @GetMapping("/results")
    public List<Map<String, Object>> getMatchResults() {
        return scraperService.getMatchResults();
    }
}
