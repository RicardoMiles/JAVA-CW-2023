package edu.uob.GameEngine;

import edu.uob.GameEntities.Character;
import edu.uob.GameEntities.Location;
import edu.uob.GameEntities.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GameState {
    private String startingLocation;
    private HashMap<String,Location> currGameMap;
    private HashMap<String, HashSet<GameAction>> gameActionsList;
    private HashMap<String, Player> playersList;


    public GameState(String startingLocation){
        this.currGameMap = new HashMap<String,Location>();
        this.startingLocation = startingLocation;
        this.gameActionsList = new HashMap<>();
        this.playersList = new HashMap<String,Player>();
    }

    public void loadGameMap(HashMap<String,Location> gameMap){
        this.currGameMap = gameMap;
    }

    public void loadPlayer(String playerName) {
        if (playersList.containsKey(playerName)) {
            System.out.println("Player Exist ! ! ! ");
        } else {
            Player newPlayer = new Player(playerName, "User controlled player");
            playersList.put(playerName, newPlayer);
            // Add the new player to the starting location's characters list
            Location startingLoc = currGameMap.get(startingLocation);
            if (startingLoc != null) {
                startingLoc.addCharacterToLocation(newPlayer);
            } else {
                System.out.println("Starting location not found in the game map!");
            }
        }
    }

    public int getPlayerHealth(String playerName) {
        Player player = playersList.get(playerName);
        if (player != null) {
            return player.getHealth();
        } else {
            return 10086;
        }
    }

    public String getCurrentLocationInfo(String playerName) {
        for (Location location : currGameMap.values()) {
            for (Character player : location.getCharacters()) {
                if (player.getName().equals(playerName)) {
                    String locationInfo = "You are in "+location.getName() + " - " + location.getDescription();
                    locationInfo += "\n\n" + location.getArtefactsList();
                    locationInfo += "\n\n" + location.getFurnitureList();
                    locationInfo += "\n\n" + location.getAccessibleLocations();
                    return locationInfo;
                }
            }
        }
        return "Login location not found for player: " + playerName;
    }

    public boolean checkLocationAccessiblity(String playerName, String targetLocation) {
        for (Location location : currGameMap.values()) {
            for (Character player : location.getCharacters()) {
                if (player.getName().equals(playerName)) {
                    return location.getPaths().contains(targetLocation);
                }
            }
        }
        return false;
    }

    public void loadGameActions(){
        this.gameActionsList = new HashMap<>();
    }

    public HashMap<String, Location> getCurrGameMap() {
        return currGameMap;
    }

    public void gotoCMD(String playerName, String targetLocation){
        Location currentLocation = null;
        Player playerToMove = null;

        for (Location location : currGameMap.values()) {
            for (Character player : location.getCharacters()) {
                if (player.getName().equals(playerName)) {
                    currentLocation = location;
                    playerToMove = (Player) player;
                    break;
                }
            }
            if (currentLocation != null){
                break;
            }
        }

        if (currentLocation != null && playerToMove != null) {
            Location targetLoc = currGameMap.get(targetLocation);
            if (targetLoc != null) {
                currentLocation.removeCharacterFromLocation(playerToMove);
                targetLoc.addCharacterToLocation(playerToMove);
                System.out.println(playerName + " has been moved to " + targetLocation);
            } else {
                System.out.println("Target location not found in the game map!");
            }
        } else {
            System.out.println("Player not found in any location!");
        }
    }



}
