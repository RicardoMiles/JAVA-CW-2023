package edu.uob.StagCommand;

import edu.uob.GameEngine.GameModel;

import java.util.ArrayList;


import edu.uob.*;
import edu.uob.GameEngine.*;
import edu.uob.StagEntities.*;
import edu.uob.StagCommand.*;


import java.util.ArrayList;

public class DynamicCommand extends PlayerCommand{
    private GameAction action;
    private ArrayList<String> subjects;
    Location currentLocation;
    Location storeroom;

    public DynamicCommand(Player player, GameModel model, GameAction action) {
        super(player, model);
        this.action = action;
        this.subjects = action.getSubjects();
        this.currentLocation = model.getLocationList().get(player.getCurrentLocation());
        this.storeroom = model.getLocationList().get("storeroom");
    }

    @Override
    public String interpretCMD()  {
        checkSubjectsExist();
        return updateGameState();
    }

    private void checkSubjectsExist()  {
        for(String s : subjects){
            if(!checkInventory(s) && !checkLocation(s))
                throw new RuntimeException("subject does not exist.\n");
        }
    }

    private boolean checkInventory(String s){
        ArrayList<GameEntity> inventory = player.getInventory();
        for(GameEntity entity : inventory){
            if(s.equals(entity.getName())) return true;
        }
        return false;
    }

    private boolean checkLocation(String s){
        for(GameEntity entity : currentLocation.getEntityList()){
            if(s.equals(entity.getName())) return true;
        }
        return false;
    }

    private String updateGameState()  {
        ArrayList<String> consumed = action.getConsumed();
        ArrayList<String> produced = action.getProduced();

        // Consume entities
        for(String consumedEntity : consumed){
            consumeEntities(consumedEntity);
        }

        // See if the player dies after consuming subjects
        if(player.isDead()){
            dieAndResurrect();
            return "you died and lost all of your items, you must return to the start of the game.\n";
        }

        // Produce entities
        if(!produced.isEmpty()) {
            for (String producedEntity : produced) {
                produceEntities(producedEntity);
            }
        }
        return action.getNarration() + "\n";
    }

    private void consumeEntities(String consumedEntity) {
        consumeHealth(consumedEntity);
        consumePath(consumedEntity);
        consumeFromPlayer(consumedEntity);
        consumeFromLocation(consumedEntity);
    }

    private void consumeHealth(String consumedEntity){
        if(consumedEntity.equalsIgnoreCase("health")){
            player.decreaseHealth();
        }
    }

    private void consumePath(String consumedEntity){
        if(currentLocation.checkIfPathPossible(consumedEntity)){
            currentLocation.getPaths().remove(consumedEntity);
        }
    }

    private void consumeFromPlayer(String consumedEntity)  {
        if(player.checkEntityInInventory(consumedEntity)){
            GameEntity subject = player.getEntityByName(consumedEntity);
            storeroom.addEntity(subject);
            player.getInventory().remove(subject);
        }
    }

    private void consumeFromLocation(String consumedEntity)  {
        if(currentLocation.checkEntityInLocation(consumedEntity)){
            GameEntity subject = currentLocation.getEntityByName(consumedEntity);
            storeroom.addEntity(subject);
            currentLocation.getEntityList().remove(subject);
        }
    }

    private void produceEntities(String producedEntity) {
        produceToLocation(producedEntity);
        produceHealth(producedEntity);
        producePath(producedEntity);
    }

    private void produceToLocation(String producedEntity)  {
        // Produce from storeroom
        ArrayList<GameEntity> storeroomEntities = storeroom.getEntityList();
        if(storeroom.checkEntityInLocation(producedEntity)){
            GameEntity entity = storeroom.getEntityByName(producedEntity);
            currentLocation.addEntity(entity);
            storeroomEntities.remove(entity);
        }

        // Produce from other locations
        for(String locationName : model.getLocationList().keySet()){
            Location checkedLocation = model.getLocationList().get(locationName);
            ArrayList<GameEntity> allEntity = checkedLocation.getEntityList();
            for(GameEntity entity : allEntity){
                if(entity.getName().equalsIgnoreCase(producedEntity)){
                    currentLocation.addEntity(entity);
                    checkedLocation.getEntityList().remove(entity);
                    break;
                }
            }
        }
    }


    private void produceHealth(String producedEntity){
        if(producedEntity.equalsIgnoreCase("health")){
            player.increaseHealth();
        }
    }

    private void producePath(String producedEntity){
        if(model.getLocationList().containsKey(producedEntity.toLowerCase())){
            currentLocation.addPath(producedEntity);
        }
    }

    private void dieAndResurrect(){
        ArrayList<GameEntity> inv = player.getInventory();
        for(GameEntity entity : inv){
            currentLocation.addEntity(entity);
        }
        player.getInventory().clear();
        player.restoreHealth();
        player.setCurrentLocation(model.getStartingLocation());
    }

}
