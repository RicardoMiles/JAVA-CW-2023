package edu.uob.StagCommand;

import edu.uob.GameEngine.GameModel;
import edu.uob.*;
import edu.uob.StagEntities.*;

import java.util.ArrayList;

public class HealthCommand extends PlayerCommand{
    public HealthCommand(Player player, GameModel model){
        super(player,model);
    }

    @Override
    public String interpretCMD(){
        StringBuilder healthChecker = new StringBuilder();
        healthChecker.append("Your health value is : \n");
        //Get current hp value of player
        int currHP = player.getHealth();
        healthChecker.append(currHP);
        healthChecker.append("\n");
        return healthChecker.toString();
    }

}

