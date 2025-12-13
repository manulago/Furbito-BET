package com.furbitobet.backend.repository;

import com.furbitobet.backend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    java.util.List<Player> findByTeam(String team);
}
