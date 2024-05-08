package edu.uob.StagCommand;

import edu.uob.GameEngine.*;
import edu.uob.*;
import edu.uob.StagEntities.*;

import java.util.ArrayList;

public class GotoCommand extends PlayerCommand{
    public GotoCommand(Player player, GameModel model, ArrayList<String> tokens) {
        super(player, model);
        this.tokens = tokens;
    }

    @Override
    public String interpretCMD() {
        syntaxCompleteCheck();
        Location currentLocation = model.getLocationList().get(player.getCurrentLocation());
        ArrayList<String> paths = currentLocation.getPaths();
        int index = getPathIndex(paths);

        String locationName = paths.get(index);
        player.setCurrentLocation(locationName);
        return "You went to " + locationName + "\n";
    }

    private int getPathIndex(ArrayList<String> paths)  {
        int index = 0;
        int count = 0;
        for(int i=0; i<paths.size(); i++){
            if(tokens.contains(paths.get(i))){
                count++;
                index = i;
            }
        }
        if(count == 0){
            throw new RuntimeException("there is no path to this place");
        }else if(count > 1){
            throw new RuntimeException("you can only go to one place each time");
        }
        return index;
    }
}
