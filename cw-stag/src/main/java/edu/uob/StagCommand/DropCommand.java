package edu.uob.StagCommand;

import edu.uob.GameEngine.*;
import edu.uob.*;
import edu.uob.StagEntities.*;

import java.util.ArrayList;

public class DropCommand extends PlayerCommand{
    public DropCommand(Player player, GameModel model, ArrayList<String> tokens) {
        super(player, model);
        this.tokens = tokens;
    }

    @Override
    public String interpretCMD()  {
        checkCommandCompleted();
        ArrayList<GameEntity> playerInventory = player.getInventory();
        int index = getEntityIndexByType(playerInventory, "artefacts", "drop");
        GameEntity artefact = player.getInventory().get(index);
        Location currentLocation = model.getLocationList().get(player.getCurrentLocation());
        currentLocation.addEntity(artefact);
        String artefactName = playerInventory.get(index).getName();
        player.removeFromInventory(artefactName);
        return "You dropped a " + artefactName + "\n";
    }
}
