package com.furbitobet.backend.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScraperService {

    private static final String URL = "https://udcxest.udc.gal/xest/publica/liga/grupo/clasificacion.xhtml?idobjsel=749";
    private static final Logger logger = LoggerFactory.getLogger(ScraperService.class);

    // Cache configuration
    private static final int CACHE_DURATION_MINUTES = 5;

    // Cache for standings
    private List<Map<String, String>> cachedStandings = null;
    private LocalDateTime standingsLastFetch = null;

    // Cache for match results
    private List<Map<String, Object>> cachedResults = null;
    private LocalDateTime resultsLastFetch = null;

    public List<Map<String, String>> getLeagueStandings() {
        // Check if cache is valid
        if (cachedStandings != null && standingsLastFetch != null) {
            long minutesSinceLastFetch = Duration.between(standingsLastFetch, LocalDateTime.now()).toMinutes();
            if (minutesSinceLastFetch < CACHE_DURATION_MINUTES) {
                logger.info("Returning cached standings (age: {} minutes)", minutesSinceLastFetch);
                return cachedStandings;
            }
        }

        logger.info("Cache expired or empty, fetching fresh standings data...");
        List<Map<String, String>> standings = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(URL)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();

            logger.info("Scraping URL: {}", URL);
            logger.info("Document Title: {}", doc.title());

            // Use the specific ID for the table wrapper
            Element tableDiv = doc.select("div[id='formPpl:tablaEquiposVinculados']").first();

            if (tableDiv != null) {
                // Check for scrollable body first
                Element scrollableBody = tableDiv.select("div.ui-datatable-scrollable-body").first();
                Element table;

                if (scrollableBody != null) {
                    table = scrollableBody.select("table").first();
                } else {
                    // Fallback to standard table
                    table = tableDiv.select("table").first();
                }

                if (table != null) {
                    Elements rows = table.select("tr");
                    logger.info("Found rows: {}", rows.size());
                    for (Element row : rows) {
                        Elements cols = row.select("td");
                        if (cols.size() >= 10) {
                            Map<String, String> teamData = new HashMap<>();
                            teamData.put("position", cols.get(0).text());
                            teamData.put("team", cols.get(1).text());
                            teamData.put("points", cols.get(2).text());
                            teamData.put("played", cols.get(4).text());
                            teamData.put("won", cols.get(5).text());
                            teamData.put("drawn", cols.get(6).text());
                            teamData.put("lost", cols.get(7).text());
                            teamData.put("gf", cols.get(8).text());
                            teamData.put("ga", cols.get(9).text());
                            standings.add(teamData);
                        }
                    }
                } else {
                    logger.warn("Table not found inside div");
                }
            } else {
                logger.warn("TableDiv not found");
            }

            // Update cache
            cachedStandings = standings;
            standingsLastFetch = LocalDateTime.now();
            logger.info("Standings cache updated with {} teams", standings.size());

        } catch (IOException e) {
            logger.error("Error scraping league standings", e);
            // If scraping fails but we have cached data, return it even if expired
            if (cachedStandings != null) {
                logger.warn("Returning stale cache due to scraping error");
                return cachedStandings;
            }
        }
        return standings;
    }

    public List<Map<String, Object>> getMatchResults() {
        // Check if cache is valid
        if (cachedResults != null && resultsLastFetch != null) {
            long minutesSinceLastFetch = Duration.between(resultsLastFetch, LocalDateTime.now()).toMinutes();
            if (minutesSinceLastFetch < CACHE_DURATION_MINUTES) {
                logger.info("Returning cached match results (age: {} minutes)", minutesSinceLastFetch);
                return cachedResults;
            }
        }

        logger.info("Cache expired or empty, fetching fresh match results...");
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            Document doc = Jsoup
                    .connect("https://udcxest.udc.gal/xest/publica/liga/partido/partidos-jornadas.xhtml?idgrupo=749")
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();

            Elements matchDays = doc.select("div.marcoPanel");

            for (Element dayPanel : matchDays) {
                String matchDayName = dayPanel.select("label.negrita").first().text();

                Elements matches = dayPanel.select("div.ui-panelgrid.bordeCssGrid");

                for (Element match : matches) {
                    Map<String, Object> matchData = new HashMap<>();
                    matchData.put("matchDay", matchDayName);

                    // Date and Time
                    String dateTime = match.select("div.ui-panelgrid-header").text();
                    matchData.put("dateTime", dateTime);

                    // Teams
                    Elements teamLabels = match.select("div.ui-panelgrid-content .ui-g .ui-panelgrid-cell").first()
                            .select("label");
                    if (teamLabels.size() >= 3) {
                        matchData.put("homeTeam", teamLabels.get(0).text());
                        matchData.put("awayTeam", teamLabels.get(2).text());
                    }

                    // Score
                    // The score is in the second row (second ui-g)
                    Elements rows = match.select("div.ui-panelgrid-content > div.ui-g");
                    if (rows.size() >= 2) {
                        String score = rows.get(1).text();
                        matchData.put("score", score);
                    }

                    results.add(matchData);
                }
            }

            // Update cache
            cachedResults = results;
            resultsLastFetch = LocalDateTime.now();
            logger.info("Match results cache updated with {} matches", results.size());

        } catch (IOException e) {
            logger.error("Error scraping match results", e);
            // If scraping fails but we have cached data, return it even if expired
            if (cachedResults != null) {
                logger.warn("Returning stale cache due to scraping error");
                return cachedResults;
            }
        }
        return results;
    }
}
