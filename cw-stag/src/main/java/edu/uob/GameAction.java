package edu.uob;

import java.util.List;
import java.util.Set;

public class GameAction {
    private Set<String> triggerPhrases;
    private Set<String> subjects;
    private Set<String> consumedEntities;
    private Set<String> producedEntities;
    private String narration;

    public GameAction(Set<String> triggerPhrases, Set<String> subjects, Set<String> consumedEntities, Set<String> producedEntities, String narration) {
        this.triggerPhrases = triggerPhrases;
        this.subjects = subjects;
        this.consumedEntities = consumedEntities;
        this.producedEntities = producedEntities;
        this.narration = narration;
    }

    // Getter methods
    public Set<String> getTriggerPhrases() {
        return triggerPhrases;
    }

    public Set<String> getSubjects() {
        return subjects;
    }

    public Set<String> getConsumedEntities() {
        return consumedEntities;
    }

    public Set<String> getProducedEntities() {
        return producedEntities;
    }

    public String getNarration() {
        return narration;
    }
}

