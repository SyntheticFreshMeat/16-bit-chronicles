package com.juego16bits.juego16bits.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SavedGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerName;
    private int level;
    private int health;
    private int experience;
    private String locationName;

    public SavedGame() {
        // requerido por JPA
    }

    public SavedGame(String playerName, int level, int health, int experience, String locationName) {
        this.playerName = playerName;
        this.level = level;
        this.health = health;
        this.experience = experience;
        this.locationName = locationName;
    }

    public Long getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getLevel() {
        return level;
    }

    public int getHealth() {
        return health;
    }

    public int getExperience() {
        return experience;
    }

    public String getLocationName() {
        return locationName;
    }
}
