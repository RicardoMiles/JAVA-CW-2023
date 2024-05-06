package edu.uob.StagCommand;

import edu.uob.GameEngine.GameModel;
import edu.uob.*;
import edu.uob.StagEntities.*;

import java.util.ArrayList;

public class LookCommand extends PlayerCommand{
    public LookCommand(Player player, GameModel model) {
        super(player, model);
    }

    @Override
    public String interpretCMD(){
        String currentLocationName = player.getCurrentLocation();
        Location currentLocation = model.getLocationList().get(currentLocationName);
        return describeLocation(currentLocationName, currentLocation);
    }

    private String describeLocation(String locationName, Location currentLocation){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You are in " + locationName + " - " + currentLocation.getDescription() + "\n\n");
        if(!currentLocation.getEntityList().isEmpty()) {
            //stringBuilder.append("You can see:\n");
            describeEntities(stringBuilder, currentLocation);
        }
        if(!currentLocation.getPaths().isEmpty()) {
            stringBuilder.append("You can access from here:\n");
            describeAccessiblePaths(stringBuilder, currentLocation);
        }
        describeOtherPlayers(stringBuilder,locationName);
        return stringBuilder.toString();
    }

    private void describeEntities(StringBuilder stringBuilder, Location currentLocation){
        ArrayList<String> allEntityTypes = getAllEntityTypes(currentLocation);
        for(String type : allEntityTypes) {
            stringBuilder.append("You can see " + type + ":\n");
            for (GameEntity entity : currentLocation.getEntityList()) {
                if(entity.getType().equals(type)) {
                    stringBuilder.append(entity.getName() + " - " + entity.getDescription() + "\n");
                }
            }
            stringBuilder.append("\n");
        }

    }

    private ArrayList<String> getAllEntityTypes(Location currentLocation){
        ArrayList<String> allEntityTypes = new ArrayList<>();
        for(GameEntity entity : currentLocation.getEntityList()){
            if(!allEntityTypes.contains(entity.getType())){
                allEntityTypes.add(entity.getType());
            }
        }
        return allEntityTypes;
    }

    private void describeAccessiblePaths(StringBuilder stringBuilder, Location currentLocation){
        for(String path : currentLocation.getPaths()){
            stringBuilder.append(path + "\n");
        }
    }

    private void describeOtherPlayers(StringBuilder stringBuilder, String locationName){
        StringBuilder players = new StringBuilder();
        for(String playerName : model.getPlayerList().keySet()){
            if(!playerName.equals(model.getCurrentPlayerName())) {
                if (model.getPlayerByName(playerName).getCurrentLocation().equals(locationName)) {
                    players.append(playerName + "\n");
                }
            }
        }
        if(!players.isEmpty()){
            stringBuilder.append("\nYou can see other players in the game:\n");
            stringBuilder.append(players);
        }
    }
}
