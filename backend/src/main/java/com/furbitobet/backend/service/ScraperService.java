package com.furbitobet.backend.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScraperService {

    private static final String URL = "https://udcxest.udc.gal/xest/publica/liga/grupo/clasificacion.xhtml?idobjsel=749";

    private static final Logger logger = LoggerFactory.getLogger(ScraperService.class);

    public List<Map<String, String>> getLeagueStandings() {
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
        } catch (IOException e) {
            logger.error("Error scraping league standings", e);
        }
        return standings;
    }

    public List<Map<String, Object>> getMatchResults() {
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
        } catch (IOException e) {
            logger.error("Error scraping match results", e);
        }
        return results;
    }
}
