package edu.uob.GameEngine;

import edu.uob.GameEntities.Artefact;
import edu.uob.GameEntities.Character;
import edu.uob.GameEntities.Location;
import edu.uob.GameEntities.Player;
import edu.uob.GameEntity;

import java.util.*;

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

    public String lookCMD(String playerName) {
        Location location = locatePlayer(playerName);
        if (location != null) {
            String locationInfo = "You are in " + location.getName() + " - " + location.getDescription();
            if(location.getArtefactsList() == ""){
                locationInfo += "";
            }else{
                locationInfo += System.lineSeparator() + System.lineSeparator() + location.getArtefactsList();
            }
            if(location.getFurnitureList() == ""){
                locationInfo += "";
            }else{
                locationInfo += System.lineSeparator() + System.lineSeparator() + location.getFurnitureList();
            }
            locationInfo += System.lineSeparator() + System.lineSeparator() + location.getAccessibleLocations() + System.lineSeparator();
            return locationInfo;
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
        Location currentLocation = locatePlayer(playerName);
        if (currentLocation != null) {
            Player playerToMove = playersList.get(playerName);
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

    public String inventoryCMD(String playerName) {
        // Find where is the player right now
        Location currentLocation = null;
        Player playerToCheck = null;
        Set<Artefact> inventoryOfPlayer = new HashSet<>();

        for (Location location : currGameMap.values()) {
            for (Character player : location.getCharacters()) {
                if (player.getName().equals(playerName)) {
                    currentLocation = location;
                    playerToCheck = (Player) player;
                    break;
                }
            }
            if (currentLocation != null){
                break;
            }
        }

        // If player exist some place
        if (currentLocation != null && playerToCheck != null) {
            // Get items in inventory
            inventoryOfPlayer= playerToCheck.getInventory();
            if (inventoryOfPlayer.isEmpty()) {
                System.out.println(playerName + "'s inventory is empty.");
                return "Your inventory is empty." + System.lineSeparator();
            } else {
                System.out.println(playerName + "'s inventory contains:");
                StringBuilder inventoryInfo = new StringBuilder("You have the following items:" + System.lineSeparator());
                for (Artefact item : inventoryOfPlayer) {
                    String itemName = item.getName();
                    String itemDescription = item.getDescription();
                    inventoryInfo.append(itemName).append(" - ").append(itemDescription).append(System.lineSeparator());
                }
                System.out.print(inventoryInfo.toString());
                return inventoryInfo.toString();
            }
        } else {
            System.out.println("Player not found in any location!");
            return "Player not found in any location!";
        }
    }

    public String getCMD(String itemName,String playerName) {
        Location currentLocation = locatePlayer(playerName);
        if (currentLocation != null) {
            Player playerToGainItem = playersList.get(playerName);
            if(itemName == null){
                return "ItemName can not be read" + System.lineSeparator();
            }else {
                Artefact itemToBeMoved = currentLocation.getArtefactByName(itemName);
                if(itemToBeMoved == null){
                    return "Getting item from current location fail ! " + System.lineSeparator();
                }
                // Add it to the player in currGameState
                playerToGainItem.addToInventory(itemToBeMoved);
                if(currentLocation.removeArtefactByName(itemName)){
                    return "You picked up a " + itemName + System.lineSeparator();
                }else{
                    return itemName + "remove bug" + System.lineSeparator();
                }
            }
        } else {
            System.out.println("Player not found in any location!");
        }
        return "You picked up a " + itemName + System.lineSeparator();
    }

    public Location locatePlayer(String playerName) {
        for (Location location : currGameMap.values()) {
            for (Character player : location.getCharacters()) {
                if (player.getName().equals(playerName)) {
                    return location;
                }
            }
        }
        return null;
    }

}
