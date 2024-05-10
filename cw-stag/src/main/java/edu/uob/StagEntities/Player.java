package edu.uob.StagEntities;

import edu.uob.*;


import java.util.ArrayList;

public class Player {
    private ArrayList<GameEntity> inventory = new ArrayList<>();
    private static final Integer MAX_HEALTH = 3;
    private int health = MAX_HEALTH;
    private String currentLocation;

    public Player(String startingLocation){
        currentLocation = startingLocation;
    }

    public String getCurrentLocation(){
        return currentLocation;
    }

    public void setCurrentLocation(String location){
        currentLocation = location;
    }

    public ArrayList<GameEntity> getInventory(){
        return inventory;
    }

    public void addToInventory(GameEntity entity){
        inventory.add(entity);
    }

    public void removeFromInventory(String entityName){
        for(GameEntity entity : inventory){
            if(entity.getName().equalsIgnoreCase(entityName)){
                inventory.remove(entity);
                return;
            }
        }
    }

    public boolean checkEntityInInventory(String entityName){
        for(GameEntity entity : inventory){
            if(entity.getName().equalsIgnoreCase(entityName)){
                return true;
            }
        }
        return false;
    }

    public GameEntity getEntityByName(String entityName) {
        for(GameEntity entity : inventory){
            if(entity.getName().equalsIgnoreCase(entityName)){
                return entity;
            }
        }
        throw new RuntimeException("there is no such entity in player inventory.\n");
    }

    public int getHealth(){
        return health;
    }
    public void increaseHealth(){
        if(health<MAX_HEALTH) health++;
    }

    public void decreaseHealth(){
        health--;
    }

    public void restoreHealth(){
        health = MAX_HEALTH;
    }

    public boolean isDead(){
        return health <= 0;
    }

}

