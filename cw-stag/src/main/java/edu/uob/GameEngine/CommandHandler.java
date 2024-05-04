package edu.uob.GameEngine;

import java.util.ArrayList;
import java.util.Arrays;

import edu.uob.GameEntity;
import edu.uob.GameServer;

public class CommandHandler {
    GameModel model;
    static ArrayList<String> builtIns = new ArrayList<>(Arrays.asList("inventory", "inv", "get", "drop", "goto", "look"));
    public CommandHandler(GameModel model){
        this.model = model;
    }

    public String parseCommand(String command){
        GameTokenizer tokenizer = new GameTokenizer(command);
        ArrayList<String> tokens = tokenizer.splitIntoTokens();
        model.setCurrentPlayer(tokenizer.getPlayerName());

        if(!checkUniqueBuiltinTrigger(tokens,tokenizer))throw new RuntimeException("不能匹配action,请再试一遍.\n");

        switch(standardizeCommand(tokens)){
            case "inventory":
                //TODO inventory command here
            case "get":
                //TODO get  command here
            case "drop":
                //TODO drop command here
            case "goto":
                //TODO goto command here
            case"look":
                //TODO look command here
        }
        return "什么基础命令都没读出来";
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
