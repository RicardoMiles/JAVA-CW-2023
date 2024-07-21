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

    public String getAccessibleLocations() {
        StringBuilder accessibleLocations = new StringBuilder("You can access these locations:");
        for (String path : paths) {
            accessibleLocations.append("\n").append(path);
        }
        return accessibleLocations.toString();
    }

    public String getArtefactsList() {
        StringBuilder artefactsList = new StringBuilder("You can see artefacts:");
        for (Artefact artefact : artefacts) {
            artefactsList.append("\n").append(artefact.getName()).append(" - ").append(artefact.getDescription());
        }
        return artefactsList.toString();
    }

    public String getFurnitureList(){
        StringBuilder furnitureList = new StringBuilder("You can see furniture:");
        for (Furniture furniture : furniture) {
            furnitureList.append("\n").append(furniture.getName()).append(" - ").append(furniture.getDescription());
        }
        return furnitureList.toString();
    }

    public void removeCharacterFromLocation(Character character) {
        characters.remove(character);
    }

}
