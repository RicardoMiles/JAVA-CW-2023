package edu.uob.GameEntities;

import edu.uob.GameEntity;

public class Character extends GameEntity {
    private int health;

    public Character(String name, String description, int health)
    {
        super(name, description);
        this.health = health;
    }

    public int getCharacterHealth()
    {
        return health;
    }

    public void setCharacterHealth(int health)
    {
        this.health = health;
    }

    //可以添加其他特定于 Character 的方法和assets
}
