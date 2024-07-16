package edu.uob.GameEngine;

import edu.uob.GameEntities.Location;

import java.util.List;

public class CurrentGameState {
    private Location startingLocation;
    private List<Location> gameMap;
    private String playerName;

    public void CurrentGameState(List<Location> gameMap, Location startingLocation){
        this.playerName = "DefaultPlayer";
        this.gameMap = gameMap;
        this.startingLocation = startingLocation;
    }
}
