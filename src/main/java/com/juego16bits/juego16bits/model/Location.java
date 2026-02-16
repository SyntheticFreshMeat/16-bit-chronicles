package com.juego16bits.juego16bits.model;

public class Location {

    private String name;
    private String description;
    private String image;

    public Location(String name, String description, String image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
}