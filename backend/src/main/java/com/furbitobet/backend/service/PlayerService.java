package com.furbitobet.backend.service;

import com.furbitobet.backend.model.Player;
import com.furbitobet.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerOddsService playerOddsService;

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    public Player updatePlayer(Long id, Player playerDetails) {
        Optional<Player> playerOptional = playerRepository.findById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            player.setName(playerDetails.getName());
            player.setTeam(playerDetails.getTeam());
            player.setPosition(playerDetails.getPosition());
            player.setGoals(playerDetails.getGoals());
            player.setAssists(playerDetails.getAssists());
            player.setMatchesPlayed(playerDetails.getMatchesPlayed());
            player.setMatchesStarted(playerDetails.getMatchesStarted());
            player.setYellowCards(playerDetails.getYellowCards());
            player.setRedCards(playerDetails.getRedCards());
            player.setMinutesPlayed(playerDetails.getMinutesPlayed());
            player.setShotsOnTarget(playerDetails.getShotsOnTarget());
            player.setKeyPasses(playerDetails.getKeyPasses());
            
            Player savedPlayer = playerRepository.save(player);
            
            // Recalcular automáticamente las cuotas cuando se actualizan estadísticas
            playerOddsService.recalculatePlayerOdds(id);
            
            return savedPlayer;
        }
        return null;
    }

    /**
     * Actualiza las estadísticas de un jugador de forma incremental.
     * Útil para actualizar después de cada partido.
     */
    public Player updatePlayerStats(Long id, int goalsScored, int assistsMade, 
                                    int minutesPlayed, boolean started, 
                                    int yellowCards, int redCards) {
        Optional<Player> playerOptional = playerRepository.findById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            
            player.setGoals(player.getGoals() + goalsScored);
            player.setAssists(player.getAssists() + assistsMade);
            player.setMatchesPlayed(player.getMatchesPlayed() + 1);
            if (started) {
                player.setMatchesStarted(player.getMatchesStarted() + 1);
            }
            player.setMinutesPlayed(player.getMinutesPlayed() + minutesPlayed);
            player.setYellowCards(player.getYellowCards() + yellowCards);
            player.setRedCards(player.getRedCards() + redCards);
            
            Player savedPlayer = playerRepository.save(player);
            
            // Recalcular cuotas automáticamente
            playerOddsService.recalculatePlayerOdds(id);
            
            return savedPlayer;
        }
        return null;
    }

    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }

    /**
     * Obtiene los jugadores de un equipo específico.
     */
    public List<Player> getPlayersByTeam(String team) {
        return playerRepository.findByTeam(team);
    }

    /**
     * Obtiene todos los equipos disponibles.
     */
    public List<String> getAllTeams() {
        return playerRepository.findAllTeams();
    }
}
