package edu.uob.GameEngine;

import edu.uob.GameAction;
import edu.uob.GameEntities.*;
import edu.uob.GameEntities.Character;
import edu.uob.GameEntity;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {
    private String startingLocation;
    private HashMap<String,Location> currGameMap;
    private HashMap<String, HashSet<GameAction>> currGameActions;
    private HashMap<String, Player> playersList;

    /**
     * Constructor to initialize the GameState with a starting location.
     *
     * @param startingLocation The default location where the game begins for every player.
     */
    public GameState(String startingLocation){
        this.currGameMap = new HashMap<String,Location>();
        this.startingLocation = startingLocation;
        this.currGameActions = new HashMap<>();
        this.playersList = new HashMap<String,Player>();
    }

    /**
     * Loads the game map with the provided locations.
     *
     * @param gameMap A HashMap containing the game's locations.
     */
    public void loadGameMap(HashMap<String,Location> gameMap){
        this.currGameMap = gameMap;
    }

    /**
     * Loads the game actions with the provided action rules.
     *
     * @param gameActionRules A HashMap containing sets of GameActions.
     */
    public void loadGameActions(HashMap<String, HashSet<GameAction>> gameActionRules){
        this.currGameActions = gameActionRules;
    }

    /**
     * Loads a player into the game, setting their starting location.
     *
     * @param playerName The name of the player to be added.
     */
    public void loadPlayer(String playerName) {
        if (!playersList.containsKey(playerName)) {
            Player newPlayer = new Player(playerName, "User controlled player");
            playersList.put(playerName, newPlayer);
            // Add the new player to the starting location's characters list
            Location startingLoc = currGameMap.get(startingLocation);
            if (startingLoc != null) {
                startingLoc.addCharacterToLocation(newPlayer);
            } else {
                System.out.println("[ERROR] Starting location not found in the game map!");
            }
        }
    }

    /**
     * Finds a player by their String attribute name.
     *
     * @param playerName The name of the player to find.
     * @return The Player object if found, otherwise null.
     */
    public Player findPlayerByName(String playerName) {
        return playersList.get(playerName);
    }

    /**
     * Retrieves the health of a player by their name.
     *
     * @param playerName The name of the player.
     * @return The health of the player if found, otherwise a default error code.
     */
    public int getPlayerHealth(String playerName) {
        Player player = playersList.get(playerName);
        if (player != null) {
            return player.getHealth();
        } else {
            return 10086;
        }
    }

    /**
     * Generates a description of the player's current location.
     *
     * @param playerName The name of the player.
     * @return A string describing the current location and its contents.
     */
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
            if (location.getCharactersList() == "") {
                locationInfo += "";
            }else{
                locationInfo += System.lineSeparator() + System.lineSeparator() + location.getCharactersList();
            }
            locationInfo += System.lineSeparator() + System.lineSeparator() + location.getAccessibleLocations() + System.lineSeparator();
            if (!location.getOtherPlayersList(playerName).isEmpty()) {
                locationInfo += System.lineSeparator() + location.getOtherPlayersList(playerName) + System.lineSeparator() ;
            }
            return locationInfo;
        }
        return "Login location not found for player: " + playerName;
    }

    /**
     * Checks if the target location is accessible from the player's current location.
     *
     * @param playerName The name of the player.
     * @param targetLocation The name of the target location.
     * @return True if the location is accessible, otherwise false.
     */
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

    /**
     * Retrieves the current game map.
     *
     * @return A HashMap containing the game's locations.
     */
    public HashMap<String, Location> getCurrGameMap() {
        return currGameMap;
    }

    public void executeGotoCMD(String playerName, String targetLocation){
        Location currentLocation = locatePlayer(playerName);
        if (currentLocation != null) {
            Player playerToMove = playersList.get(playerName);
            Location targetLoc = currGameMap.get(targetLocation);
            if (targetLoc != null) {
                currentLocation.removeCharacterFromLocation(playerToMove);
                targetLoc.addCharacterToLocation(playerToMove);
            } else {
                System.out.println("[ERROR] Target location not found in the game map!");
            }
        } else {
            System.out.println("[ERROR] Player not found in any location!");
        }
    }

    public String executeInventoryCMD(String playerName) {
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
        // if (currentLocation != null && playerToCheck != null) {
        if (currentLocation != null) {
            // Get items in inventory
            inventoryOfPlayer= playerToCheck.getInventory();
            if (inventoryOfPlayer.isEmpty()) {
                return "Your inventory is empty." + System.lineSeparator();
            } else {
                StringBuilder inventoryInfo = new StringBuilder("You have the following items:" + System.lineSeparator());
                for (Artefact item : inventoryOfPlayer) {
                    String itemName = item.getName();
                    String itemDescription = item.getDescription();
                    inventoryInfo.append(itemName).append(" - ").append(itemDescription).append(System.lineSeparator());
                }
                return inventoryInfo.toString();
            }
        } else {
            System.out.println("[ERROR] Player not found in any location!");
            return "Player not found in any location!";
        }
    }

    public String executeGetCMD(String itemName,String playerName) {
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
            System.out.println("[ERROR] Player not found in any location!");
            return "Player not found in any location!";
        }
    }

    public String executeDropCMD(String itemName,String playerName){
        Location currentLocation = locatePlayer(playerName);
        if (currentLocation != null) {
            Player playerToDropItem = playersList.get(playerName);
            if(itemName == null){
                return "ItemName can not be read" + System.lineSeparator();
            }else{
                Artefact itemToBeMoved = playerToDropItem.getArtefactByName(itemName);
                if (itemToBeMoved == null){
                    return "Copy item from inventory fail. "+ System.lineSeparator();
                }else{
                    currentLocation.addArtefact(itemToBeMoved);
                    if(playerToDropItem.removeFromInventoryByName(itemName)){
                        return "You dropped a " + itemName + "." + System.lineSeparator();
                    }else{
                        return "Delete operation after copy from inventory fail." + System.lineSeparator();
                    }
                }
            }
        } else {
            return "Player not found in any location!";
        }
    }

    /**
     * Locates the player's current location.
     *
     * @param playerName The name of the player.
     * @return The Location object where the player is currently located.
     */
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

    /**
     * Retrieves all trigger phrases from the game actions.
     *
     * @return A set of all trigger phrases.
     */
    public Set<String> getAllTriggerPhrases() {
        Set<String> allTriggerPhrases = new HashSet<>();
        for (HashSet<GameAction> actions : currGameActions.values()) {
            for (GameAction action : actions) {
                allTriggerPhrases.addAll(action.getTriggerPhrases());
            }
        }
        return allTriggerPhrases;
    }

    /**
     * Retrieves all actions associated with a specific trigger.
     *
     * @param trigger The trigger phrase.
     * @return A set of GameActions linked to the trigger.
     */
    public Set<GameAction> getActionsForTrigger(String trigger) {
        return currGameActions.getOrDefault(trigger, new HashSet<>());
    }

    /**
     * Checks if the player and their location contain all specified entities.
     *
     * @param playerName The name of the player.
     * @param entities A set of entity names to check.
     * @return True if all entities are present, otherwise false.
     */
    public boolean checkPlayerAndLocationHaveAllEntities(String playerName, Set<String> entities) {
        Player player = playersList.get(playerName);
        if (player == null) {
            return false;
        }

        Location location = locatePlayer(playerName);
        if (location == null) {
            return false;
        }

        Set<String> inventory = player.getInventory().stream().map(Artefact::getName).collect(Collectors.toSet());
        Set<String> locationEntities = new HashSet<>();
        locationEntities.addAll(location.getArtefacts().stream().map(Artefact::getName).collect(Collectors.toSet()));
        locationEntities.addAll(location.getCharacters().stream().map(Character::getName).collect(Collectors.toSet()));
        locationEntities.addAll(location.getFurniture().stream().map(GameEntity::getName).collect(Collectors.toSet()));

        Set<String> combinedEntities = new HashSet<>(inventory);
        combinedEntities.addAll(locationEntities);

        return combinedEntities.containsAll(entities);
    }

    /**
     * Performs the specified action by loaded .XML file.
     *
     * @param action The GameAction to perform.
     * @param playerName The name of the player performing the action.(due to multiplayer feature)
     */
    public void performActionFromFiles(GameAction action, String playerName) {
        Set<String> consumedEntities = action.getConsumedEntities();
        Set<String> producedEntities = action.getProducedEntities();

        // Get player and current location
        Player player = playersList.get(playerName);
        Location location = locatePlayer(playerName);
        Location storeroom = currGameMap.get("storeroom");

        // Consuming entities
        for (String entity : consumedEntities) {
            if ("health".equals(entity)) {
                player.decreaseHealth(1);
            }else if (player.getInventory().stream().anyMatch(item -> item.getName().equals(entity))) {
                Artefact item = player.getArtefactByName(entity);
                player.removeFromInventory(item);
                if (storeroom != null) {
                    storeroom.addArtefact(item);
                }
            } else if (location.getArtefacts().stream().anyMatch(item -> item.getName().equals(entity))) {
                Artefact item = location.getArtefactByName(entity);
                location.removeArtefactByName(entity);
                if (storeroom != null) {
                    storeroom.addArtefact(item);
                }
            } else if (location.getFurniture().stream().anyMatch(furniture -> furniture.getName().equals(entity))) {
                Furniture furnitureItem = location.getFurniture().stream().filter(furniture -> furniture.getName().equals(entity)).findFirst().orElse(null);
                if (furnitureItem != null) {
                    location.getFurniture().remove(furnitureItem);
                }
                if (storeroom != null) {
                    storeroom.addFurniture(furnitureItem);
                }
            } else {
                // Check if the entity is a hidden location
                if (location.getPaths().contains(entity)) {
                    location.removePathFromLocation(entity);
                }
            }
        }

        // Producing entities
        for (String entity : producedEntities) {
            if ("health".equals(entity)) {
                player.increaseHealth(1);
            } else if (storeroom != null) {
                // Check and move Artefacts from storeroom
                Artefact artefact = storeroom.getArtefactByName(entity);
                if (artefact != null) {
                    storeroom.removeArtefactByName(entity);
                    location.addArtefact(artefact);
                } else {
                    // Check and move Furniture from storeroom
                    Furniture furnitureItem = storeroom.getFurniture().stream().filter(furniture -> furniture.getName().equals(entity)).findFirst().orElse(null);
                    if (furnitureItem != null) {
                        storeroom.getFurniture().remove(furnitureItem);
                        location.addFurniture(furnitureItem);
                    }else{
                        // Check and move Character from storeroom
                        Character character = storeroom.getCharacters().stream().filter(c -> c.getName().equals(entity)).findFirst().orElse(null);
                        if (character != null) {
                            storeroom.removeCharacterFromLocation(character);
                            location.addCharacterToLocation(character);
                        } else {
                            // If it is generated and pulled from store room
                            for (Location loc : currGameMap.values()) {
                                Character existingCharacter = loc.getCharacters().stream().filter(c -> c.getName().equals(entity)).findFirst().orElse(null);
                                if (existingCharacter != null) {
                                    loc.removeCharacterFromLocation(existingCharacter);
                                    location.addCharacterToLocation(existingCharacter);
                                    break;
                                }
                            }
                            // Check if it is a possible path to be locked
                            if (currGameMap.containsKey(entity)) {
                                location.addPathToLocation(entity);
                            }
                        }
                    }
                }
            }
        }
    }

    public void dropAllItems(String playerName) {
        Player player = playersList.get(playerName);
        Location location = locatePlayer(playerName);

        if (player != null && location != null) {
            Set<Artefact> inventory = player.getInventory();
            for (Artefact item : inventory) {
                location.addArtefact(item);
            }
            inventory.clear();
        }
    }

    public String getStartingLocation(){
        return startingLocation;
    }

    public Set<String> getAllEntitiesName() {
        Set<String> entitiesInGame = new HashSet<>();

        // Get all location name
        for (Location location : currGameMap.values()) {
            entitiesInGame.add(location.getName());

            // Get all Artefacts' name from location
            for (Artefact artefact : location.getArtefacts()) {
                entitiesInGame.add(artefact.getName());
            }

            // Get all Furniture's name from location
            for (Furniture furniture : location.getFurniture()) {
                entitiesInGame.add(furniture.getName());
            }

        }

        // Get inventory items' name from all players
        for (Player player : playersList.values()) {
            for (Artefact artefact : player.getInventory()) {
                entitiesInGame.add(artefact.getName());
            }
        }

        return entitiesInGame;
    }

}
