package edu.uob.GameEngine;

import java.util.ArrayList;
import java.util.Arrays;

import edu.uob.*;
import edu.uob.StagCommand.*;
import edu.uob.StagEntities.*;

public class CommandHandler {
    GameModel model;
    PlayerCommand playerCMD;
    static ArrayList<String> builtIns = new ArrayList<>(Arrays.asList("inventory", "inv", "get", "drop", "goto", "look"));
    public CommandHandler(GameModel model){
        this.model = model;
    }
/*
    public String parseCommand(String command){
        GameTokenizer tokenizer = new GameTokenizer(command);
        ArrayList<String> tokens = tokenizer.splitIntoTokens();
        model.setCurrentPlayer(tokenizer.getPlayerName());
        Player player = model.getPlayerByName(tokenizer.getPlayerName());

        if(!checkUniqueBuiltinTrigger(tokens,tokenizer))throw new RuntimeException("不能匹配action,请再试一遍.\n");

        switch(standardizeCommand(tokens)){
            case "inventory":
                //TODO inventory command here
            case "get":
                //TODO get  command here
                playerCMD = new GetCommand(player, model, tokens);
            case "drop":
                //TODO drop command here
            case "goto":
                //TODO goto command here
            case"look":
                //TODO look command here
        }
        return "bloody hell";
    }
*/
    public String parseCommand(String command) {
        try {
            GameTokenizer tokenizer = new GameTokenizer(command);
            ArrayList<String> tokens = tokenizer.splitIntoTokens();
            if (tokens.isEmpty()) {
                return "Error: No input provided.\n";
            }
            model.setCurrentPlayer(tokenizer.getPlayerName());
            Player player = model.getPlayerByName(tokenizer.getPlayerName());

            if (!checkUniqueBuiltinTrigger(tokens, tokenizer)) {
                return "Error: Action cannot be matched, please try again.\n";
            }

            switch (standardizeCommand(tokens)) {
                case "inventory","inv":
                    playerCMD = new InventoryCommand(player, model);
                    break;
                case "get":
                    playerCMD = new GetCommand(player, model, tokens);
                    break;
                case "drop":
                    playerCMD = new DropCommand(player, model, tokens);
                    break;
                case "goto":
                    playerCMD = new GotoCommand(player, model, tokens);
                    break;
                case "look":
                    playerCMD = new LookCommand(player, model);
                    break;
                default:
                    return "Error: Command not recognized.\n";
            }
            return playerCMD.interpretCMD();
        } catch (Exception e) {
            return "Server Error: " + e.getMessage() + "\n";
        }
    }


    private String standardizeCommand(ArrayList<String> tokens){
        for(String token :tokens){
            if(builtIns.contains(token)){
                return token;
            }
        }
        return "动态";
    }

    private boolean checkUniqueBuiltinTrigger(ArrayList<String> tokens, GameTokenizer tokenizer){
        ArrayList<String> potentialBuiltinTriggers = new ArrayList<>();
        for(String token : tokens){
            if(builtIns.contains(token)){
                potentialBuiltinTriggers.add(token);
            }
        }

        DynamicActionParser parser = new DynamicActionParser(model, tokens, tokenizer.getCommandsWithoutPlayer());

        if(potentialBuiltinTriggers.size() == 0) {
            if(parser.getValidActionNum() == 1) {
                return true;
            } else if(parser.getValidActionNum() > 1){
                throw new RuntimeException("There are more than one possible actions, please specify.\n");
            }
        } else if (potentialBuiltinTriggers.size() == 1){
            if(parser.getValidActionNum() == 0){
                return true;
            }else throw new RuntimeException("There are more than one possible actions, please specify.\n");
        } else throw new RuntimeException("There are more than one possible actions, please specify.\n");
        return false;
    }

}
