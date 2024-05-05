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

    public void checkCommandCompleted()  {
        if(tokens.size() <=1 ){
            throw new RuntimeException("the command is incomplete.\n");
        }
    }

    public int getEntityIndexByType(ArrayList<GameEntity> entityList, String type, String builtinCommand)  {
        int count = 0;
        int index = 0;
        for(int i=0; i<entityList.size(); i++){
            if(entityList.get(i).getType().equals(type)) {
                if (tokens.contains(entityList.get(i).getName())) {
                    count++;
                    index = i;
                }
            }
        }
        if(count == 0){
            throw new RuntimeException("cannot find the " + type);
        }else if(count > 1) {
            throw new RuntimeException("there is more than one thing you can \'" + builtinCommand + "\' here - which one do you want?\n");
        }
        return index;
    }

}
