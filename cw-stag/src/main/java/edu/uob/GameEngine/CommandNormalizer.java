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

        // 步骤 1：处理第一个“:”之前的内容
        this.playerName = "";
        int colonIndex = incomingCommand.indexOf(':');
        if (colonIndex != -1) {
            this.playerName = incomingCommand.substring(0, colonIndex).replaceAll("\\s+", "");
        }

        // 步骤 2：处理第一个“:”之后的内容
        String remainingCommand = "";
        if (colonIndex != -1 && colonIndex + 1 < incomingCommand.length()) {
            // Preprocessing ： adapt to TASK 8 Command Flexibility
            remainingCommand = incomingCommand.substring(colonIndex + 1).trim().toLowerCase();
        }

        // 使用空格分割字符串，并存储到列表中
        this.commandParts = new ArrayList<>();
        String[] parts = remainingCommand.split("\\s+");
        for (String part : parts) {
            commandParts.add(part);
        }

        // 输出测试
        System.out.println("Command Parts: " + commandParts);

        this.matchedCommand = findMatchedCommand(commandParts);
    }

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

    public String outputPlayerName() {
        return playerName;
    }

    public String outputMatchedCommand() {
        return matchedCommand;
    }

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

    public String checkItemToDrop(String playerName){
        List<String> matchedArtefacts = new ArrayList<>();
        Player playerToDropItem = findPlayerByName(playerName);

        if (playerToDropItem != null) {
            for (String commandPart : commandParts){
                Set<Artefact> inventory = playerToDropItem.getInventory();
                for (Artefact artefact : inventory) {
                    if(artefact.getName().equals(commandPart)){
                        matchedArtefacts.add(commandPart);
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

    public List<String> getCommandParts() {
        return commandParts;
    }

    // Method to check for extraneous locations in the command
    public boolean hasExtraneousLocationNames(Set<String> actionSubjects, Set<String> actionConsumed, Set<String> actionProduced) {
        for (String part : commandParts) {
            if (currGameMap.containsKey(part) && !actionSubjects.contains(part) && !actionConsumed.contains(part) && !actionProduced.contains(part)) {
                return true;
            }
        }
        return false;
    }

    // Method to check for extraneous artefacts in the command
    public boolean hasExtraneousArtefactNames(Set<String> actionSubjects, Set<String> actionConsumed, Set<String> actionProduced) {
        for (String part : commandParts) {
            boolean found = false;
            for (Location location : currGameMap.values()) {
                for (Artefact artefact : location.getArtefacts()) {
                    if (artefact.getName().equals(part)) {
                        found = true;
                        break;
                    }
                }
            }
            if (found && !actionSubjects.contains(part) && !actionConsumed.contains(part) && !actionProduced.contains(part)) {
                return true;
            }
        }
        return false;
    }

    // Method to check for extraneous furniture in the command
    public boolean hasExtraneousFurnitureNames(Set<String> actionSubjects, Set<String> actionConsumed, Set<String> actionProduced) {
        for (String part : commandParts) {
            boolean found = false;
            for (Location location : currGameMap.values()) {
                for (Furniture furniture : location.getFurniture()) {
                    if (furniture.getName().equals(part)) {
                        found = true;
                        break;
                    }
                }
            }
            if (found && !actionSubjects.contains(part) && !actionConsumed.contains(part) && !actionProduced.contains(part)) {
                return true;
            }
        }
        return false;
    }

}
