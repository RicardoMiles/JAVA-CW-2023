package edu.uob.GameEntities;

import edu.uob.GameEntity;

public class Artefact extends GameEntity {
    private int value;

    public Artefact(String name, String description, int value){
        super(name,description);
        this.value = value;
    }

    public int getArtefactValue(){
        return value;
    }

    public void setArtefactValue(int value){
        this.value = value;
    }

    // 可以添加其他特定于Artefact的方法和asset
}
