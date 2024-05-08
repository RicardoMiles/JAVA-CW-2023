package edu.uob.GameEngine;

import edu.uob.*;
import edu.uob.StagEntities.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DynamicActionParser {
    GameModel model;
    ArrayList<String> tokens;
    String commandsNoPlayer;
    GameAction action;
    String trigger;
    ArrayList<String> triggerList = new ArrayList<>();

    public DynamicActionParser(GameModel model, ArrayList<String> tokens, String commandsNoPlayer) {
        this.model = model;
        this.tokens = tokens;
        this.commandsNoPlayer = commandsNoPlayer;
    }

    public GameAction getAction()  {
        Set<String> triggers = model.getActionList().keySet();
        if(!verifyTriggerPhrase(triggers)) throw new RuntimeException("Unable to find a valid trigger.\n");

        ArrayList<GameAction> potentialActions = getPotentialActionsByTrigger(trigger);
        if(potentialActions.size() == 0){
            throw new RuntimeException("Sorry, Unable to match an executable action to the valid trigger.\n");
        }else if(potentialActions.size() > 1){
            throw new RuntimeException("Ambiguous command is found, please try again.\n");
        } else {
            action = potentialActions.get(0);
        }
        return action;
    }


    public int getValidActionNum() {
        Set<String> triggers = model.getActionList().keySet();
        verifyTriggerPhrase(triggers);

        if(triggerList.size()>1){
            for(String t : triggerList){
                ArrayList<GameAction> potentialActions = getPotentialActionsByTrigger(t);
                if(potentialActions.size()!=1) triggerList.remove(t);
            }
        }
        return triggerList.size();
    }

    // Check if there is a valid trigger in command
    private boolean verifyTrigger(){
        boolean containTrigger = false;
        Set<String> triggers = model.getActionList().keySet();
        for(String token : tokens){
            if(triggers.contains(token.toLowerCase())){
                trigger = token;
                triggerList.add(token);
                containTrigger = true;
            }
        }
        return containTrigger;
    }

    private boolean verifyTriggerPhrase(Set<String> triggers){
        boolean containTriggerPhrase = false;
        for(String t : triggers){
            // If the trigger is a phrase, there is space in it
            if(t.contains(" ")){
                if(commandsNoPlayer.toLowerCase().contains(t)){
                    trigger = t;
                    triggerList.add(t);
                    containTriggerPhrase = true;
                }
            }
        }
        if(verifyTrigger()) containTriggerPhrase = true;
        return containTriggerPhrase;
    }

    // Check if ALL subject entities in each action that matches the trigger are available to the player
    public ArrayList<GameAction> getPotentialActionsByTrigger(String trigger)  {
        HashSet<GameAction> actionsMatchTrigger = model.getActionList().get(trigger);
        ArrayList<GameAction> potentialActions = new ArrayList<>();
        ArrayList<String> availableEntityNames = getAvailableEntityNames();
        for(GameAction action : actionsMatchTrigger){
            ArrayList<String> subjects = action.getSubjects();
            if(availableEntityNames.containsAll(subjects) && checkAtLeastOneSubjectInCommand(subjects)){
                potentialActions.add(action);
            }
        }
        return potentialActions;
    }

    private ArrayList<GameEntity> getAvailableEntities(){
        ArrayList<GameEntity> availableEntities = new ArrayList<>();

        // Get ALL the subject entities from Player Inventory
        String playerName = model.getCurrentPlayerName();
        Player player = model.getPlayerByName(playerName);
        availableEntities.addAll(player.getInventory());

        // Get ALL the subject entities from current Location
        String currentLocationName = player.getCurrentLocation();
        Location currentLocation = model.getLocationList().get(currentLocationName);
        availableEntities.addAll(currentLocation.getEntityList());

        return availableEntities;
    }

    private ArrayList<String> getAvailableEntityNames(){
        ArrayList<GameEntity> availableEntities = getAvailableEntities();
        ArrayList<String> availableEntityNames = new ArrayList<>();
        for(GameEntity entity : availableEntities){
            availableEntityNames.add(entity.getName());
        }
        return availableEntityNames;
    }

    private boolean checkAtLeastOneSubjectInCommand(ArrayList<String> subjects){
        for(String subject : subjects){
            if(tokens.contains(subject)){
                return true;
            }
        }
        return false;
    }

}
