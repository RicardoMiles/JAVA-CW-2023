package edu.uob.GameEntities;

import edu.uob.GameEntity;

import java.util.HashSet;
import java.util.Set;

public class Player extends Character{
    private int health;
    private Set<Artefact> inventory;

    public Player(String name, String description){
        super(name,description);
        this.health = 3;
        this.inventory = new HashSet<>();
    }

    public Set<Artefact> getInventory() {
        return inventory;
    }

    public void addToInventory(Artefact inventoryItem) {
        inventory.add(inventoryItem);
    }

    public void removeFromInventory(Artefact inventoryItem) {
        inventory.remove(inventoryItem);
    }

    public boolean removeFromInventoryByName(String artefactName) {
        for (Artefact artefact : inventory) {
            if (artefact.getName().equals(artefactName)) {
                inventory.remove(artefact);
                return true; // Artefact found and removed
            }
        }
        return false; // Artefact not found
    }

    public Artefact getArtefactByName(String name) {
        for (Artefact artefact : inventory) {
            if (artefact.getName().equals(name)) {
                return artefact; // Artefact found
            }
        }
        return null; // Artefact not found
    }

    // Support for health command and feature
    public int getHealth() {
        return health;
    }

    public void increaseHealth(int increment) {
        if (health < 3) {
            health += increment;
            if (health > 3) {
                health = 3;
            }
        }
    }

    public void decreaseHealth(int decrement) {
        health -= decrement;
        if (health < 0) {
            health = 0;
        }
    }

}
