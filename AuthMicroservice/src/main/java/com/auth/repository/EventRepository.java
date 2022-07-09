package com.auth.repository;

import com.auth.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event save(Event event);
    List<Event> findAll();
}
