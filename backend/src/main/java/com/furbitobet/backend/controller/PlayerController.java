package com.furbitobet.backend.controller;

import com.furbitobet.backend.model.Player;
import com.furbitobet.backend.service.PlayerService;
import com.furbitobet.backend.service.PlayerOddsService;
import com.furbitobet.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "http://localhost:5173")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerOddsService playerOddsService;

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        Player player = playerService.getPlayerById(id);
        if (player != null) {
            return ResponseEntity.ok(player);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Player createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody Player playerDetails) {
        Player updatedPlayer = playerService.updatePlayer(id, playerDetails);
        if (updatedPlayer != null) {
            // Recalcular cuotas del jugador actualizado
            playerOddsService.recalculatePlayerOdds(id);
            return ResponseEntity.ok(updatedPlayer);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene las estadísticas y cuotas calculadas de un jugador.
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<PlayerOddsService.PlayerStatsDTO> getPlayerStats(@PathVariable Long id) {
        PlayerOddsService.PlayerStatsDTO stats = playerOddsService.getPlayerStats(id);
        if (stats != null) {
            return ResponseEntity.ok(stats);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Obtiene los top goleadores.
     */
    @GetMapping("/top-scorers")
    public List<Player> getTopScorers() {
        return playerRepository.findTopScorers();
    }

    /**
     * Obtiene los top asistentes.
     */
    @GetMapping("/top-assisters")
    public List<Player> getTopAssisters() {
        return playerRepository.findTopAssisters();
    }

    /**
     * Obtiene los jugadores de un equipo específico.
     */
    @GetMapping("/team/{team}")
    public List<Player> getPlayersByTeam(@PathVariable String team) {
        return playerRepository.findByTeam(team);
    }

    /**
     * Obtiene todos los equipos disponibles.
     */
    @GetMapping("/teams")
    public List<String> getAllTeams() {
        return playerRepository.findAllTeams();
    }

    /**
     * Recalcula las cuotas de un jugador específico.
     */
    @PostMapping("/{id}/recalculate-odds")
    public ResponseEntity<Map<String, Object>> recalculatePlayerOdds(@PathVariable Long id) {
        Player player = playerService.getPlayerById(id);
        if (player == null) {
            return ResponseEntity.notFound().build();
        }

        playerOddsService.recalculatePlayerOdds(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cuotas recalculadas para " + player.getName());
        response.put("player", player);
        response.put("stats", playerOddsService.getPlayerStats(id));

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene jugadores con mejor ratio de goles (mínimo partidos configurables).
     */
    @GetMapping("/best-goal-rate")
    public List<Player> getPlayersByBestGoalRate(@RequestParam(defaultValue = "3") int minMatches) {
        return playerRepository.findByBestGoalRate(minMatches);
    }

    /**
     * Obtiene jugadores con mejor ratio de asistencias.
     */
    @GetMapping("/best-assist-rate")
    public List<Player> getPlayersByBestAssistRate(@RequestParam(defaultValue = "3") int minMatches) {
        return playerRepository.findByBestAssistRate(minMatches);
    }
}
