package edu.uob.GameEntities;


import edu.uob.GameEntity;


import java.util.ArrayList;
import java.util.List;

public class Location extends GameEntity {
    private List<String> paths;
    private List<Character> characters;
    private List<Artefact> artefacts;
    private List<Furniture> furniture;
    public Location(String name, String description)
    {
        super(name,description);
        this.paths = new ArrayList<>();
        this.characters = new ArrayList<>();
        this.artefacts = new ArrayList<>();
        this.furniture = new ArrayList<>();
    }

    //其他特定于Location的方法和assets
    public void addPathToLocation(String locationName){
        paths.add(locationName);
    }

    public void findAccessibleLocation(List<PathPair> pathsInEntitiesFile){
        List<String> accessibleLocations = new ArrayList<>();
        for (PathPair path : pathsInEntitiesFile) {
            if (path.getStartLocationFromPath().equals(this.getName())) {
                paths.add(path.getEndLocationFromPath());
            }
        }
    }

    public boolean checkPathAccessibility(Location location){
        return paths.contains(location.getName());
    }

    public void addCharacterToLocation(Character character){
        characters.add(character);
    }

    public void addArtefact(Artefact artefact){
        artefacts.add(artefact);
    }

    public void addFurniture(Furniture furnitureItem){
        furniture.add(furnitureItem);
    }

    public List<String> getPaths(){
        return paths;
    }

    public List<Character> getCharacters(){
        return characters;
    }

    public List<Artefact> getArtefacts(){
        return artefacts;
    }

    public List<Furniture> getFurniture(){
        return furniture;
    }
}
