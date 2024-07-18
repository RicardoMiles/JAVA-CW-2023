package edu.uob.GameEngine;

import edu.uob.GameEntity;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class GameActionManager {
    private HashMap<String, HashSet<GameAction>> actions;

    public GameActionManager() {
        this.actions = new HashMap<>();
    }

    public void addGameAction(GameAction action) {
        if (action != null) {
            for (String trigger : action.getTriggerPhrases()) {
                actions.computeIfAbsent(trigger, k -> new HashSet<>()).add(action);
            }
        }
    }

    public void removeGameAction(GameAction action) {
        if (action != null) {
            for (String trigger : action.getTriggerPhrases()) {
                Set<GameAction> actionSet = actions.get(trigger);
                if (actionSet != null) {
                    actionSet.remove(action);
                    if (actionSet.isEmpty()) {
                        actions.remove(trigger);
                    }
                }
            }
        }
    }

    public Set<GameAction> findActionsByTriggerPhrase(String phrase) {
        return actions.getOrDefault(phrase, new HashSet<>());
    }

    
}
