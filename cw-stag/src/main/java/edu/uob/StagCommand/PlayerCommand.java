package edu.uob.StagCommand;

import edu.uob.GameEngine.GameModel;
import edu.uob.GameEntity;
import edu.uob.StagEntities.Player;

import java.util.ArrayList;

public class PlayerCommand {
    ArrayList<String> tokens;
    GameModel model;
    Player player;

    public PlayerCommand(Player player, GameModel model) {
        this.player = player;
        this.model = model;
    }

    public String interpretCMD()  {
        return "";
    }

    public void syntaxCompleteCheck()  {
        if(tokens.size() <=1 ){
            throw new RuntimeException("the command is incomplete.\n");
        }
    }

    public int getEntityIndexByType(ArrayList<GameEntity> entityList, String type, String builtinCommand)  {
        int matchCount = 0;
        int foundIndex = 0;

        for(int i=0; i<entityList.size(); i++){
            if(entityList.get(i).getType().equals(type)) {
                if (tokens.contains(entityList.get(i).getName())) {
                    matchCount++;
                    foundIndex = i;
                }
            }
        }

        //exception handling
        if(matchCount == 0){
            throw new RuntimeException("cannot find the " + type);
        }else if(matchCount > 1) {
            throw new RuntimeException("there is more than one thing you can \'" + builtinCommand + "\' here - which one do you want?\n");
        }
        return foundIndex;
    }

}
