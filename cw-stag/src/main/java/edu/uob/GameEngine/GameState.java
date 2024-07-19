package edu.uob.GameEngine;

import edu.uob.GameEntities.Location;
import edu.uob.GameEntities.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GameState {
    private String startingLocation;
    private List<Location> gameMap;
    private HashMap<String, HashSet<GameAction>> gameActionsList;
    private List<Player> playersList;


    public GameState(String startingLocation){
        this.gameMap = new ArrayList<>();
        this.startingLocation = startingLocation;
        this.gameActionsList = new HashMap<>();
        this.playersList = new ArrayList<>();
    }

    public void loadGameMap(List<Location> locationsList){
        this.gameMap = locationsList;
    }

    public void loadGameActions(){
        this.gameActionsList = new HashMap<>();
    }

    public void playerLogin(String playerName){
        // 检查传进来的玩家名字是否已经存在，是否是当前玩家

        // 是的话，初始化并添加到玩家列表进行维护
        // 不是的话，就啥也不做
    }

}
