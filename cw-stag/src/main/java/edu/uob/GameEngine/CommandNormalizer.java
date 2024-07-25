package edu.uob.GameEngine;

import edu.uob.GameEntities.Artefact;
import edu.uob.GameEntities.Furniture;
import edu.uob.GameEntities.Location;
import edu.uob.GameEntities.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class CommandNormalizer {
    private String matchedCommand;
    private String playerName;

    private List<String> flexibleCommands; // Flexible commands list

    private HashMap<String, Location> currGameMap; // Current game map

    public List<String> commandParts ;

    public CommandNormalizer(String incomingCommand, GameState currGameState) {
        this.flexibleCommands = new ArrayList<>(currGameState.getAllTriggerPhrases());
        this.currGameMap = new HashMap<>();

        // Step 1: Process content before the first ":"
        this.playerName = "";
        int colonIndex = incomingCommand.indexOf(':');
        if (colonIndex != -1) {
            this.playerName = incomingCommand.substring(0, colonIndex).replaceAll("\\s+", "");
        }

        // Step 2: Process content after the first ":"
        String remainingCommand = "";
        if (colonIndex != -1 && colonIndex + 1 < incomingCommand.length()) {
            // Preprocessing ： adapt to TASK 8 Command Flexibility
            remainingCommand = incomingCommand.substring(colonIndex + 1).trim().toLowerCase();
        }

        // Split the string by spaces and store it in a list
        this.commandParts = new ArrayList<>();
        String[] parts = remainingCommand.split("\\s+");
        for (String part : parts) {
            commandParts.add(part);
        }

        this.matchedCommand = findMatchedCommand(commandParts);
    }

    /**
     * Finds the matched command from the list of command parts.
     * Test if multiple commands or not.
     *
     * @param commandParts List of command parts to search for matched command.
     * @return The matched command or an error message if conflicts or no matches are found.
     */
    public String findMatchedCommand(List<String> commandParts) {
        // Built-in commands
        List<String> predefinedCommands = List.of("get", "drop", "goto", "look", "inventory", "inv", "health");

        // Compare the segments of commands
        String matchedCommand = null;
        int matchCount = 0;

        for (String commandPart : commandParts) {
            for (String predefinedCommand : predefinedCommands) {
                if (commandPart.equals(predefinedCommand)) {
                    matchCount++;
                    if (matchCount == 1) {
                        matchedCommand = predefinedCommand;
                    } else {
                        matchedCommand = "Conflicted unsupported multiple command";
                        break;
                    }
                }
            }
            for (String flexibleCommand : flexibleCommands){
                if(commandPart.equals(flexibleCommand)){
                    matchCount++;
                    if(matchCount == 1){
                        matchedCommand = flexibleCommand;
                    }else{
                        matchedCommand = "Conflicted unsupported multiple command";
                        break;
                    }
                }
            }
            if (matchCount > 1) {
                matchedCommand = "Conflicted unsupported multiple command";
                break;
            }
        }

        if (matchCount == 0) {
            matchedCommand = "No matched command";
        }

        // Output the matched result
        System.out.println("Matched Command: " + matchedCommand);

        return matchedCommand;
    }

    /**
     * Returns the player's name extracted from the command.
     * Using by multiplayer feature to differ players
     *
     * @return The player's name.
     */
    public String outputPlayerName() {
        return playerName;
    }

    /**
     * Returns the normalized command after parsing the command parts.
     *
     * @return The matched command.
     */
    public String outputMatchedCommand() {
        return matchedCommand;
    }

    /**
     * Finds the target location for the "goto" command from the command parts.
     * Avoid invalid location and multiple locations.
     *
     * @return The name of the target location or an error message if the target is invalid.
     */
    public String findGotoTarget() {
        List<String> matchedLocations = new ArrayList<>();
        for (String commandPart : commandParts) {
            if (currGameMap.containsKey(commandPart)) {
                matchedLocations.add(commandPart);
            }
        }
        if (matchedLocations.size() == 1) {
            return currGameMap.get(matchedLocations.get(0)).getName();
        } else {
            return "Invalid goto targeting location";
        }
    }

    /**
     * Checks the items to be taken from the current location based on the command parts.
     * Avoid invalid items, double command and extraneous entities.
     *
     * @return The name of the item to be taken, or an error message if no valid item is found.
     */
    public String checkItemsToBeTaken(){
        List<String> matchedArtefacts = new ArrayList<>();
        for (String commandPart : commandParts){
            for (Location location : currGameMap.values()) {
                List<Artefact> artefacts = location.getArtefacts();
                for (Artefact artefact : artefacts) {
                    if(artefact.getName().equals(commandPart)){
                        matchedArtefacts.add(commandPart);
                    }
                }
            }
        }
        if (matchedArtefacts.size() == 1){
            System.out.println("Cool! Only One Valid item");
            return matchedArtefacts.get(0);
        } else if(matchedArtefacts.size() >= 2){
            System.out.println("There is more than one thing you can get here - which one do you want" + System.lineSeparator());
            return ("MultipleItem");
        } else{
            System.out.println("No valid item to pick up." + System.lineSeparator());
            return ("NoItem");
        }
    }

    /**
     * Checks the items to be dropped by the player based on the command parts.
     * Avoid extraneous entities for drop command and entity does not exist.
     *
     * @param playerName The name of the player who is dropping the items.
     * @return The name of the item to be dropped, or an error message if no valid item is found.
     */
    public String checkItemsToDrop(String playerName){
        List<String> matchedArtefacts = new ArrayList<>();
        Player playerToDropItem = findPlayerByName(playerName);

        if (playerToDropItem != null) {
            List<String> unmatchedCommandParts = new ArrayList<>();
            // Check in player's inventory
            for (String commandPart : commandParts){
                Set<Artefact> inventory = playerToDropItem.getInventory();
                boolean matched = false;
                for (Artefact artefact : inventory) {
                    if(artefact.getName().equals(commandPart)){
                        matched = true;
                        matchedArtefacts.add(commandPart);
                    }
                }
                if (!matched) {
                    unmatchedCommandParts.add(commandPart);
                }
            }

            // Check in all locations if not found in inventory
            for (String commandPart : unmatchedCommandParts) {
                for (Location location : currGameMap.values()) {
                    List<Artefact> artefacts = location.getArtefacts();
                    for (Artefact artefact : artefacts) {
                        if (artefact.getName().equals(commandPart)) {
                            matchedArtefacts.add(commandPart);
                        }
                    }
                }
            }

        } else {
            return "Player not found in any location!" ;
        }

        if (matchedArtefacts.size() == 1){
            System.out.println("Cool! Only One Valid item");
            return matchedArtefacts.get(0);
        } else if(matchedArtefacts.size() >= 2){
            System.out.println("There is more than one thing you can get here - which one do you want" + System.lineSeparator());
            return ("MultipleItem");
        } else{
            System.out.println("No valid item to pick up." + System.lineSeparator());
            return ("NoItem");
        }
    }

    /**
     * Imports the current game map for use in command pre-processing.
     *
     * @param importedGameMap The game map to be imported.
     */
    public void importGameMap(HashMap<String, Location> importedGameMap){
        this.currGameMap = importedGameMap;
    }

    public Player findPlayerByName(String playerName) {
        for (Location location : currGameMap.values()) {
            Player player = location.findPlayerByName(playerName);
            if (player != null) {
                return player;
            }
        }
        return null;
    }

    /**
     * Returns the list of command parts extracted from the incoming command.
     * For more flexible further processing in GameServer
     *
     * @return The list of command parts.
     */
    public List<String> getCommandParts() {
        return commandParts;
    }

}
