package edu.uob.ConfigFilesReader;

import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import edu.uob.GameEntities.*;
import edu.uob.GameEntities.Character;


public class DotReader {
    public String startingLocation;

    public List<Location> locationsInEntitiesfile = new ArrayList<>();
    public ArrayList<PathPair> pathsInEntitiesfile = new ArrayList<>();
    public HashMap<String,Location> nameMappingLocationList = new HashMap<>();

    public DotReader(File entitiesFile) throws ParseException, FileNotFoundException {
        Parser parser = new Parser();
        FileReader reader = new FileReader(entitiesFile);
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();
        loadLocationsFromDot(sections);
        loadPathsFromDot(sections);
    }

    private void loadLocationsFromDot(ArrayList<Graph> sections){
        // The locations will always be in the first subgraph
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();

        // The starting location will always be in the first location in sub-subgraph
        Graph firstLocation = locations.get(0);
        Node firstLocationDetails = firstLocation.getNodes(false).get(0);

        // Get the ID twice is compulsory for exporting the location name String
        this.startingLocation = firstLocationDetails.getId().getId();
        //this.startingLocation = locations.get(0).getNodes(false).get(0).getId().getId();

        // Iterate through each location subgraph
        for(Graph location : locations){
            Node currentLocationDetails = location.getNodes(false).get(0);

            // Collecting the name and description of the location itself
            String currentLocationName = currentLocationDetails.getId().getId();
            String currentLocationDescription = currentLocationDetails.getAttribute("description");

            // Creating new location by the info collected
            Location currentLocation = new Location(currentLocationName,currentLocationDescription);

            // Recursively add entities into location
            ArrayList<Graph> entities = location.getSubgraphs();
            loadEntitiesFromLocation(entities, currentLocation);

            // Mapping Location with LocationName
            // Adding Location into LocationList
            nameMappingLocationList.put(currentLocationName,currentLocation);
            locationsInEntitiesfile.add(currentLocation);
        }

    }

    private void loadEntitiesFromLocation(ArrayList<Graph> entities, Location location){
        for (Graph entity : entities){
            ArrayList<Node> entityNodes = entity.getNodes(false);
            String entityFamily = entity.getId().getId();
            for(Node node : entityNodes){
                /* Add entity into the location */
                switch (entityFamily) {
                    case "furniture":
                        String furName = node.getId().getId();
                        String furDescription = node.getAttribute("description");
                        Furniture currFurniture = new Furniture(furName,furDescription);
                        location.addFurniture(currFurniture);
                        break;
                    case "artefacts":
                        String artefactName = node.getId().getId();
                        String artefactDescription = node.getAttribute("description");
                        Artefact currArtefact = new Artefact(artefactName,artefactDescription);
                        location.addArtefact(currArtefact);
                        break;
                    case "characters":
                        String characterName = node.getId().getId();
                        String characterDescription = node.getAttribute("description");
                        Character currCharacter = new Character(characterName,characterDescription);
                        location.addCharacterToLocation(currCharacter);
                        break;
                }
            }
        }
    }

    private void loadPathsFromDot(ArrayList<Graph> sections){
        // The paths will always be in the second subgraph
        ArrayList<Edge> paths = sections.get(1).getEdges();

        // Store the path pair into the pathlist
        for (int i = 0; i < paths.size(); i++) {
            Edge edge = paths.get(i);
            Node fromLocation = edge.getSource().getNode();
            String sourceName = fromLocation.getId().getId();
            Node toLocation = edge.getTarget().getNode();
            String targetName = toLocation.getId().getId();
            pathsInEntitiesfile.add(new PathPair(sourceName, targetName));
        }
        transPairToPath(pathsInEntitiesfile);
    }

    private void transPairToPath(ArrayList<PathPair> pathsPairs){
        for (PathPair pathPair : pathsPairs) {
            String startLocation = pathPair.getStartLocationFromPath();
            String endLocation = pathPair.getEndLocationFromPath();
            // Check if the start location exists in the nameMappingLocationList
            if (nameMappingLocationList.containsKey(startLocation)) {
                // Get the Location object and add the end location to its paths
                Location location = nameMappingLocationList.get(startLocation);
                location.addPathToLocation(endLocation);
            }
        }
    }

    public HashMap<String, Location> getGameMap() {
        return nameMappingLocationList;
    }

}
