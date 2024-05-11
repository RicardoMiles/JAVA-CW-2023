package edu.uob.GameEntities;

import edu.uob.GameEntity;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameEntity {
    private String name;
    private String description;
    private int health;
    private List<Artefacts> playerInventory;
    private ArrayList<GameEntity> playerInv;

    public Player(String name, String description){
        super(name,description);
        this.health = 3;
        this.playerInventory = new ArrayList<>();

    }

    public int getCurrentHealth(Player player){
        return player.health;
    }

    // 提高健康值
    public void increaseHealth(int amount) {
        this.health += amount;
    }

    // 降低健康值
    public void decreaseHealth(int amount) {
        this.health -= amount;
    }

    // 获取当前健康值
    public int getHealth() {
        return health;
    }

    // 获取玩家的物品清单
    public List<Artefacts> getPlayerInventory() {
        return playerInventory;
    }

    // 添加物品到玩家的物品清单
    public void addItemToInventory(Artefacts item) {
        playerInventory.add(item);
    }

    // 从玩家的物品清单中移除物品
    public void removeItemFromInventory(Artefacts item) {
        playerInventory.remove(item);
    }

}
