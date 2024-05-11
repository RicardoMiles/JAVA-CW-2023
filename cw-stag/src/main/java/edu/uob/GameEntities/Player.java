package edu.uob.GameEntities;

import edu.uob.GameEntity;

import java.util.ArrayList;

public class Player extends GameEntity {
    private String name;
    private String description;
    private int health = 3;
    private ArrayList<GameEntity> playerInv;

    public Player(String name, String description){
        super(name,description);
        health = this.health;
        playerInv = this.playerInv;

    }

    public int getHealthValue(Player player){
        return player.health;
    }

    public ArrayList<GameEntity> getInvContent(Player player){
        return player.playerInv;
    }

}
