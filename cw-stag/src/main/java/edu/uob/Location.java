package edu.uob;

import java.util.ArrayList;

public class Location {
    private String description;
    private ArrayList<GameEntity> entityList = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();

    public Location(String description){
        this.description = description;
    }

    public void addEntity(GameEntity newEntity) {
        entityList.add(newEntity);
    }

    public void removeEntity(String entityName){
        for(int i=0; i<entityList.size(); i++){
            if(entityList.get(i).getName().equalsIgnoreCase(entityName)){
                entityList.remove(i);
            }
        }
    }

    public ArrayList<GameEntity> getEntityList(){
        return entityList;
    }

    public void addPath(String newPath){
        paths.add(newPath);
    }

    public ArrayList<String> getPaths(){
        return paths;
    }

    public String getDescription(){
        return description;
    }

    public boolean checkIfPathPossible(String pathName){
        for(String p : paths){
            if(p.equalsIgnoreCase(pathName)){
                return true;
            }
        }
        return false;
    }

    public boolean checkEntityInLocation(String entityName){
        for(GameEntity entity : entityList){
            if(entity.getName().equalsIgnoreCase(entityName)){
                return true;
            }
        }
        return false;
    }

    public GameEntity getEntityByName(String entityName)  {
        for(GameEntity entity : entityList){
            if(entity.getName().equalsIgnoreCase(entityName)){
                return entity;
            }
        }
        throw new RuntimeException("there is no such entity in player inventory.\n");
    }


}
