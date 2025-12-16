package com.furbitobet.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    // Store match result for re-resolution if needed
    private Integer homeGoals;
    private Integer awayGoals;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Outcome> outcomes;

    public enum EventStatus {
        UPCOMING, LIVE, FINISHED, COMPLETED, CANCELLED
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return name; // Mapping description to name as per usage
    }

    public void setDescription(String description) {
        this.name = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getDeadline() {
        return date; // Mapping deadline to date
    }

    public void setDeadline(LocalDateTime deadline) {
        this.date = deadline;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public List<Outcome> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<Outcome> outcomes) {
        this.outcomes = outcomes;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
