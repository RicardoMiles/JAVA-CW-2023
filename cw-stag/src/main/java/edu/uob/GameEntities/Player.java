package edu.uob.GameEntities;

import edu.uob.GameEntity;

import java.util.HashSet;
import java.util.Set;

public class Player extends Character{
    private int health;
    private Set<GameEntity> inventory;

    public Player(String name, String description){
        super(name,description);
        this.health = 3;
        this.inventory = new HashSet<>();
    }

    public Set<GameEntity> getInventory() {
        return inventory;
    }

    public void addToInventory(GameEntity inventoryItem) {
        inventory.add(inventoryItem);
    }

    public void removeFromInventory(GameEntity inventoryItem) {
        inventory.remove(inventoryItem);
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
