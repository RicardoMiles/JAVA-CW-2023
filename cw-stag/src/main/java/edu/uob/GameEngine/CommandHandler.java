package edu.uob.GameEngine;

import java.util.ArrayList;
import java.util.Arrays;

import edu.uob.*;
import edu.uob.StagCommand.*;
import edu.uob.StagEntities.*;

public class CommandHandler {
    GameModel model;
    PlayerCommand playerCMD;
    static ArrayList<String> builtIns = new ArrayList<>(Arrays.asList("inventory", "inv", "get", "drop", "goto", "look", "health"));
    public CommandHandler(GameModel model){
        this.model = model;
    }

    public String parseCommand(String command) {
        try {
            GameTokenizer tokenizer = new GameTokenizer(command);
            ArrayList<String> tokens = tokenizer.basicStringDealToCommands();
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
                case "flexible":
                    DynamicActionParser actionParser = new DynamicActionParser(model, tokens, tokenizer.getCommandsWithoutPlayer());
                    GameAction action = actionParser.getAction();
                    playerCMD = new DynamicCommand(player, model, action);
                    break;
                case "health":
                    playerCMD = new HealthCommand(player,model);
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
        return "flexible";
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
