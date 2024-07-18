package edu.uob.GameEngine;

import edu.uob.GameEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class GameAction {
    private List<String> triggerPhrases;
    private Set<GameEntity> subjectEntities;
    private Set<GameEntity> consumedEntities;
    private Set<GameEntity> producedEntities;
    private String narration;

    public GameAction(List<String> triggerPhrases, Set<GameEntity> subjectEntities, Set<GameEntity> consumedEntities, Set<GameEntity> producedEntities, String narration) {
        this.triggerPhrases = (triggerPhrases != null) ? triggerPhrases : new ArrayList<>();
        this.subjectEntities = (subjectEntities != null) ? subjectEntities : new HashSet<>();
        this.consumedEntities = (consumedEntities != null) ? consumedEntities : new HashSet<>();
        this.producedEntities = (producedEntities != null) ? producedEntities : new HashSet<>();
        this.narration = narration;
    }

    public List<String> getTriggerPhrases() {
        return triggerPhrases;
    }

    public Set<GameEntity> getSubjectEntities() {
        return subjectEntities;
    }

    public Set<GameEntity> getConsumedEntities() {
        return consumedEntities;
    }

    public Set<GameEntity> getProducedEntities() {
        return producedEntities;
    }

    public String getNarration() {
        return narration;
    }

}
