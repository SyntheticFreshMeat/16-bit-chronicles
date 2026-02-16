package com.juego16bits.juego16bits.model;

public class ItemStack {

    private String id;   // herbs, ancient_relic...
    private String name; // Hierbas
    private int quantity;


    public ItemStack() {}

    public ItemStack(String id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public String getId() { return id;}
    public String getName() { return name; }
    public int getQuantity() { return quantity; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}