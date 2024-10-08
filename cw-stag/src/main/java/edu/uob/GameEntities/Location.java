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

    public void addPathToLocation(String locationName){
        paths.add(locationName);
    }

    public void removePathFromLocation(String locationName){
        paths.remove(locationName);
    }

    public void addCharacterToLocation(Character character){
        if (!characters.contains(character)) {
            characters.add(character);
        }
    }

    public void addArtefact(Artefact artefact){
        artefacts.add(artefact);
    }

    public void addFurniture(Furniture furnitureItem){
        furniture.add(furnitureItem);
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
        if (artefacts.isEmpty()) {
            return ""; // Artefacts list is empty, return an empty string
        }
        StringBuilder artefactsList = new StringBuilder("You can see artefacts:");
        for (Artefact artefact : artefacts) {
            artefactsList.append(System.lineSeparator()).append(artefact.getName()).append(" - ").append(artefact.getDescription());
        }
        return artefactsList.toString();
    }

    public String getFurnitureList(){
        if (furniture.isEmpty()) {
            return ""; // Furniture list is empty, return an empty string
        }
        StringBuilder furnitureList = new StringBuilder("You can see furniture:");
        for (Furniture furniture : furniture) {
            furnitureList.append(System.lineSeparator()).append(furniture.getName()).append(" - ").append(furniture.getDescription());
        }
        return furnitureList.toString();
    }

    public List<String> getPaths(){
        return paths;
    }

    public void removeCharacterFromLocation(Character character) {
        characters.remove(character);
    }

    public boolean removeArtefactByName(String artefactName) {
        for (Artefact artefact : artefacts) {
            if (artefact.getName().equals(artefactName)) {
                return artefacts.remove(artefact);
            }
        }
        return false;
    }

    public String getCharactersList() {
        if (characters.isEmpty()) {
            return ""; // Characters list is empty, return an empty string
        }
        StringBuilder charactersList = new StringBuilder();
        for (Character character : characters) {
            if (!(character instanceof Player)) {
                if (charactersList.length() == 0) {
                    charactersList.append("You can see characters:");
                }
                charactersList.append(System.lineSeparator()).append(character.getName()).append(" - ").append(character.getDescription());
            }
        }
        // If there is no character other than players, return ""
        if (charactersList.length() == 0) {
            return "";
        }
        return charactersList.toString();
    }

    public String getOtherPlayersList(String excludePlayerName) {
        StringBuilder playersList = new StringBuilder();
        for (Character character : characters) {
            if (character instanceof Player && !character.getName().equals(excludePlayerName)) {
                if (playersList.length() == 0) {
                    playersList.append("You can see other players in the game:");
                }
                playersList.append(System.lineSeparator()).append(character.getName());
            }
        }
        return playersList.toString();
    }
    
    public Artefact getArtefactByName(String artefactName) {
        for (int i = 0; i < artefacts.size(); i++) {
            Artefact artefact = artefacts.get(i);
            if (artefact.getName().equals(artefactName)) {
                return artefact;
            }
        }
        return null;
    }

    public Player findPlayerByName(String playerName) {
        for (int i = 0; i < characters.size(); i++) {
            Character character = characters.get(i);
            if (character.getName().equals(playerName) && character instanceof Player) {
                return (Player) character;
            }
        }
        return null;
    }

}
