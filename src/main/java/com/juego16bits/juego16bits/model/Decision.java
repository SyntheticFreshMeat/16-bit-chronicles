package com.juego16bits.juego16bits.model;

public class Decision {
    
    private String text;    // lo que ve el jugador
    private String nextLocation; // a qué localización va si elige esto
    private int healthChange; // puede subir o bajar la salud
    private int experienceChange; // puede aumentar la experiencia

    public Decision(String text, String nextLocation, int healthChange, int experienceChange) {
        this.text = text;
        this.nextLocation = nextLocation;
        this.healthChange = healthChange;
        this.experienceChange = experienceChange;
    }

    // getters
    public String getText() { return text; }
    public String getNextLocation() { return nextLocation;}
    public int getHealthChange() { return healthChange; }
    public int getExperienceChange() { return experienceChange;}
}