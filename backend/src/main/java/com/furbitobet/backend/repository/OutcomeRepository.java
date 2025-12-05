package com.furbitobet.backend.repository;

import com.furbitobet.backend.model.Outcome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutcomeRepository extends JpaRepository<Outcome, Long> {
    java.util.List<Outcome> findByEventId(Long eventId);
}
