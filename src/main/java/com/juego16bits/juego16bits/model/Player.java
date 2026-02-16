package com.juego16bits.juego16bits.model;
import java.util.ArrayList;
import java.util.List;


public class Player {
    private String name;
    private int level;
    private int health;
    private int experience;

    // he añadido una mochila
    private int backpackCapacity = 8; // slots, como en resident evil
    private final List<ItemStack> backpack = new ArrayList<>();


    public Player(String name) {
        this.name = name;
        this.level = 1;
        this.health = 100;
        this.experience = 0;
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getHealth() { return health; }
    public int getExperience() { return experience; }

    public void setLevel(int level) { this.level = level; }
    public void setHealth(int health) { this.health = health; }
    public void setExperience(int experience) { this.experience = experience; }

    // getters mochila para el JSON y el front
    public int getBackpackCapacity() { return backpackCapacity; }
    public List<ItemStack> getBackpack() { return backpack; }
    public int getBackpackUsedSlots() { return backpack.size(); }
    public boolean isBackpackFull() { return backpack.size() >= backpackCapacity; }

    public void setBackpackCapacity(int backpackCapacity) {
        this.backpackCapacity = backpackCapacity;
    }

    /*
    Añade ítem a la mochila. 
    Si ya existe el itemId, apila sin ocupar slot nuevo.
    si no existe, requiere slot libre. */

    public boolean addToBackpack(String itemId, String itemName, int qty) {
        if (qty <= 0) return false;

        for (ItemStack s : backpack) {
            if (s.getId().equals(itemId)) {
                s.setQuantity(s.getQuantity() + qty);
                return true;
            }
        }

        if (isBackpackFull()) return false;

        backpack.add(new ItemStack(itemId, itemName, qty));
        return true;
    }

    /*
    quita cantidad de un ítem.
    Si llega a 0, elimina el stack.
     */
    public boolean removeFromBackpack(String itemId, int qty) {
        if (qty <= 0) return false;

        for (int i = 0; i < backpack.size(); i++) {
            ItemStack s = backpack.get(i);
            if (s.getId().equals(itemId)) {
                int newQty = s.getQuantity() - qty;
                if (newQty > 0) {
                    s.setQuantity(newQty);
                } else {
                    backpack.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void gainExperience(int exp) {
        experience += exp;
        checkLevelUp();
    }

    public void takeDamage(int dmg) {
        health -= dmg;
        if (health < 0) health = 0;
        if (health > 100) health = 100;
    }

    private void checkLevelUp() {
        while (experience >= level * 100) {
            experience -= level * 100;
            level++;
            health = 100;
        }
    }
}
