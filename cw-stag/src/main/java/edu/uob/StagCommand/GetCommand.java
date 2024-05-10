package edu.uob.StagCommand;

import edu.uob.GameEngine.GameModel;
import edu.uob.GameEntity;
import edu.uob.Location;
import edu.uob.StagEntities.Player;
import edu.uob.StagCommand.*;


import java.util.ArrayList;

public class GetCommand extends PlayerCommand{

    public GetCommand(Player player, GameModel model, ArrayList<String> tokens) {
        super(player, model);
        this.tokens = tokens;
    }

    @Override
    public String interpretCMD()  {
        syntaxCompleteCheck();
        Location currentLocation = model.getLocationList().get(player.getCurrentLocation());
        ArrayList<GameEntity> entityList = currentLocation.getEntityList();

        // Check if current location has the artefact to be picked up
        int index = getEntityIndexByType(entityList, "artefacts", "get");

        GameEntity artefact = entityList.get(index);
        player.addToInventory(artefact);
        String artefactName = entityList.get(index).getName();
        currentLocation.removeEntity(artefactName);
        return "You picked up a " + artefactName + "\n";
    }

}
