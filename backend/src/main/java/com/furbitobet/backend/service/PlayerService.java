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
            player.setGoals(playerDetails.getGoals());
            player.setAssists(playerDetails.getAssists());
            player.setMatchesPlayed(playerDetails.getMatchesPlayed());
            player.setMatchesStarted(playerDetails.getMatchesStarted());
            player.setYellowCards(playerDetails.getYellowCards());
            player.setRedCards(playerDetails.getRedCards());
            return playerRepository.save(player);
        }
        return null;
    }

    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }
}
