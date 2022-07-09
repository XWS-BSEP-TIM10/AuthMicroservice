package com.auth.model;

import javax.persistence.*;

@Entity
@Table(name="EVENT")
public class Event {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name="name")
    String description;

    public Event() {
    }

    public Event(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
