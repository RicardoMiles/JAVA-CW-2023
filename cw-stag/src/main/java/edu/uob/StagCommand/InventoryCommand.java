package edu.uob.StagCommand;

import edu.uob.GameEngine.GameModel;
import edu.uob.GameEntity;
import edu.uob.StagEntities.Player;

import java.util.ArrayList;

public class InventoryCommand extends PlayerCommand{
    public InventoryCommand(Player player, GameModel model) {
        super(player, model);
    }

    @Override
    public String interpretCMD() {
        StringBuilder allArtefacts = new StringBuilder();
        ArrayList<GameEntity> playerInventory = player.getInventory();
        if (playerInventory.isEmpty()) {
            allArtefacts.append("Your inventory is empty.");
        } else {
            allArtefacts.append("You have the following items:\n");
            for (GameEntity entity : playerInventory) {
                if (entity.getType().equals("artefacts")) {
                    allArtefacts.append(entity.getName() + " - " + entity.getDescription() + "\n");
                }
            }
        }
        return allArtefacts.toString();
    }
}
