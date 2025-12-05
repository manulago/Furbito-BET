package com.furbitobet.backend.repository;

import com.furbitobet.backend.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUserId(Long userId);

    List<Bet> findByStatus(Bet.BetStatus status);

    List<Bet> findDistinctByOutcomes_Event_Id(Long eventId);
}
