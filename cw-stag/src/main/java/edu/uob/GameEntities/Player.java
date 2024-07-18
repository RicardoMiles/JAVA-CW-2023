package edu.uob.GameEntities;

import edu.uob.GameEntity;

import java.util.HashSet;
import java.util.Set;

public class Player extends Character{
    private int health;
    private Set<GameEntity> inventory;

    public Player(String name, String description, int health){
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

    //可以添加其他特定于 Player的方法和assets
}
