package edu.uob.GameEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GameTokenizer {
    private String command;
    private String playerName;

    private String currentToken;

    private Boolean duplicateOrNot;
    private String commandsWithoutPlayer;

    public GameTokenizer(String command){
        this.command = command;
    }

    public ArrayList<String> basicStringDealToCommands(){
        // Get the player's name
        ArrayList<String> tokens = new ArrayList<>();
        String[] commandArray = command.split(":",2);
        playerName = commandArray[0];

        String commands = commandArray[1].trim();
        this.commandsWithoutPlayer = commands;

        // Remove all punctuations
        String[] tokenArray = commands.toLowerCase().split("[-,.:!?()]|\\s+");
        Collections.addAll(tokens, tokenArray);
        tokens.removeAll(Arrays.asList("", null));

        StringBuilder tempTokenList = new StringBuilder();
        for(String token : tokens){
            tempTokenList.append(token + " ");
        }
        tempTokenList.delete(tempTokenList.length()-1,tempTokenList.length());
        this.commandsWithoutPlayer = tempTokenList.toString();
        return tokens;
    }

    public String getPlayerName(){
        return playerName;
    }

    public String getCommandsWithoutPlayer(){
        return commandsWithoutPlayer;
    }

}

