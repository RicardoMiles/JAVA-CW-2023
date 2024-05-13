package edu.uob.ConfigFileReader;


import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.GameEntities.Artefacts;
import edu.uob.GameEntities.Characters;
import edu.uob.GameEntities.Furniture;
import edu.uob.GameEntities.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class DotReader {
    private String startingLocation;
    private HashMap<String, Location> locationList = new HashMap<>();

    public DotReader(File entitiesFile) throws FileNotFoundException, ParseException{
        Parser parser = new Parser();
        try {
            FileReader reader = new FileReader(entitiesFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("EntitiesFile.dot NOT FOUND! ");
        }
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();

        loadLocations(sections);
        loadPaths(sections);
    }

    private void loadLocations(ArrayList<Graph> sections){
        // The locations will always be in the first subgraph
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();

        // The starting location is the first location in entitiesFile
        this.startingLocation = locations.get(0).getNodes(false).get(0).getId().getId();

        for(Graph location : locations){
            Node locationDetails = location.getNodes(false).get(0);
            String locationName = locationDetails.getId().getId().toLowerCase();
            Location newLocation = new Location(locationName,locationDetails.getAttribute("description"));
            ArrayList<Graph> entities = location.getSubgraphs();

            // Load entities of each location
            for(Graph entity : entities){
                ArrayList<Node> entityNodes = entity.getNodes(false);
                String type = entity.getId().getId();
                for (Node node : entityNodes) {
                    switch(type){
                        case "artefacts":
                            Artefacts newArtefact = new Artefacts(node.getId().getId(), node.getAttribute("description"));
                            newLocation.addItem(newArtefact);
                        case "furniture":
                            Furniture newFurniture = new Furniture(node.getId().getId(), node.getAttribute("description"));
                            newLocation.addFurniture(newFurniture);
                        case "characters":
                            Characters newCharacter;
                            newCharacter = new Characters(node.getId().getId(), node.getAttribute("description"));
                            newLocation.addCharacter(newCharacter);
                    }
                }
            }
            locationList.put(locationName, newLocation);
        }

    }

    private void loadPaths(ArrayList<Graph> sections){
        // The paths will always be in the second subgraph
        ArrayList<Edge> paths = sections.get(1).getEdges();
        for(Edge path : paths){
            // Start
            Node fromLocation = path.getSource().getNode();
            String fromName = fromLocation.getId().getId().toLowerCase();
            Location fromLoc = locationList.get(fromName);
            // Target
            Node toLocation = path.getTarget().getNode();
            String toName = toLocation.getId().getId().toLowerCase();
            if(!locationList.containsKey(fromName) || !locationList.containsKey(toName)){
                throw new RuntimeException("Can not create paths between two locations.\n");
            }
            // Add path for fromLocation
            fromLoc.addPath(toName);
            System.out.println("From" + fromLocation.getId() + " To " + toLocation.getId()+ " is available ");
        }
    }

    public HashMap<String, Location> getLocationList() {
        return this.locationList;
    }

    public String getStartingLocation() {
        return this.startingLocation;
    }

}
