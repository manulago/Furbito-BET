package com.furbitobet.backend.repository;

import com.furbitobet.backend.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUserId(Long userId);

    List<Bet> findByStatus(Bet.BetStatus status);

    List<Bet> findDistinctByOutcomes_Event_Id(Long eventId);

    @org.springframework.data.jpa.repository.Query("SELECT b.user.id, SUM(b.winnings), SUM(b.amount) FROM Bet b WHERE b.status IN ('WON', 'LOST', 'VOID') GROUP BY b.user.id")
    List<Object[]> findNetProfitStats();
}
