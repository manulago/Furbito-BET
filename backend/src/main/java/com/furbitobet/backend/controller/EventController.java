package com.furbitobet.backend.controller;

import com.furbitobet.backend.model.Event;
import com.furbitobet.backend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    /**
     * Regenera las cuotas de jugadores para un evento.
     * Útil cuando se actualizan las estadísticas de los jugadores.
     */
    @PostMapping("/{id}/regenerate-player-odds")
    public ResponseEntity<Map<String, Object>> regeneratePlayerOdds(@PathVariable Long id) {
        try {
            eventService.regeneratePlayerOdds(id);
            Event event = eventService.getEventById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cuotas de jugadores regeneradas correctamente");
            response.put("event", event);
            response.put("outcomesCount", event.getOutcomes() != null ? event.getOutcomes().size() : 0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error regenerando cuotas: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Autowired
    private com.furbitobet.backend.service.BetService betService;

    @GetMapping("/{id}/winning-bets")
    public java.util.List<com.furbitobet.backend.dto.EventResultDTO> getEventResults(@PathVariable Long id) {
        return betService.getEventResults(id);
    }
}
