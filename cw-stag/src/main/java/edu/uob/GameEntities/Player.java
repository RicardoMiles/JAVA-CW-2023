package edu.uob.GameEntities;

public class Player extends Character{
    private int health;

    public Player(String name, String description, int health){
        super(name,description);
        this.health = 3;
    }

    //可以添加其他特定于 Player的方法和assets
}
