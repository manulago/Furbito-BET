package com.furbitobet.backend.repository;

import com.furbitobet.backend.model.Outcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutcomeRepository extends JpaRepository<Outcome, Long> {
    
    List<Outcome> findByEventId(Long eventId);
    
    // Buscar outcomes por grupo
    List<Outcome> findByEventIdAndOutcomeGroup(Long eventId, String outcomeGroup);
    
    // Buscar outcomes pendientes de un evento
    List<Outcome> findByEventIdAndStatus(Long eventId, Outcome.OutcomeStatus status);
    
    // Buscar outcomes que contengan un texto en la descripción
    List<Outcome> findByDescriptionContainingIgnoreCase(String description);
    
    // Buscar outcomes de goleadores pendientes
    @Query("SELECT o FROM Outcome o WHERE o.outcomeGroup = 'Goleadores' AND o.status = 'PENDING'")
    List<Outcome> findPendingScorerOutcomes();
    
    // Buscar outcomes de asistencias pendientes
    @Query("SELECT o FROM Outcome o WHERE o.outcomeGroup = 'Asistencias' AND o.status = 'PENDING'")
    List<Outcome> findPendingAssistOutcomes();
    
    // Buscar outcomes de un jugador específico (por nombre en descripción)
    @Query("SELECT o FROM Outcome o WHERE o.description LIKE %:playerName% AND o.status = 'PENDING'")
    List<Outcome> findPendingOutcomesByPlayerName(@Param("playerName") String playerName);
    
    // Contar outcomes por grupo para un evento
    @Query("SELECT o.outcomeGroup, COUNT(o) FROM Outcome o WHERE o.event.id = :eventId GROUP BY o.outcomeGroup")
    List<Object[]> countOutcomesByGroupForEvent(@Param("eventId") Long eventId);
    
    // Eliminar outcomes de un grupo específico para un evento
    void deleteByEventIdAndOutcomeGroup(Long eventId, String outcomeGroup);
}
