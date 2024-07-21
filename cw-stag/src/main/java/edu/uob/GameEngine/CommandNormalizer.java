package edu.uob.GameEngine;

import edu.uob.GameEntities.Artefact;
import edu.uob.GameEntities.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CommandNormalizer {
    private String matchedCommand;
    private String playerName;

    private List<String> flexibleCommands; // Flexible commands list

    private HashMap<String, Location> currGameMap; // Current game map

    public List<String> commandParts ;

    public CommandNormalizer(String incomingCommand) {
        this.flexibleCommands = new ArrayList<>();
        this.currGameMap = new HashMap<>();
        // Preprocessing ： adapt to TASK 8 Command Flexibility
        incomingCommand = incomingCommand.toLowerCase();


        // 步骤 1：处理第一个“:”之前的内容
        this.playerName = "";
        int colonIndex = incomingCommand.indexOf(':');
        if (colonIndex != -1) {
            this.playerName = incomingCommand.substring(0, colonIndex).replaceAll("\\s+", "");
        }

        // 步骤 2：处理第一个“:”之后的内容
        String remainingCommand = "";
        if (colonIndex != -1 && colonIndex + 1 < incomingCommand.length()) {
            remainingCommand = incomingCommand.substring(colonIndex + 1).trim();
        }

        // 使用空格分割字符串，并存储到列表中
        this.commandParts = new ArrayList<>();
        String[] parts = remainingCommand.split("\\s+");
        for (String part : parts) {
            commandParts.add(part);
        }

        // 输出测试
        System.out.println("Player Name: " + playerName);
        System.out.println("Command Parts: " + commandParts);

        this.matchedCommand = findMatchedCommand(commandParts);
    }

    public String findMatchedCommand(List<String> commandParts) {
        // 预定义的字符串列表
        List<String> predefinedCommands = List.of("get", "drop", "goto", "look", "inventory", "inv", "health");

        // 比对命令部分
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
            if (matchCount > 1) {
                matchedCommand = "Conflicted unsupported multiple command";
                break;
            }
        }

        if (matchCount == 0) {
            matchedCommand = "No matched command";
        }

        // 输出匹配结果
        System.out.println("Matched Command: " + matchedCommand);

//        switch(matchedCommand){
//            case "get":
//                commandParts;
//        }

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

    public void importGameMap(HashMap<String, Location> importedGameMap){
        this.currGameMap = importedGameMap;
    }

}
