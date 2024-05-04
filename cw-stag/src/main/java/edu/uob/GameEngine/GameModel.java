package edu.uob.GameEngine;

import edu.uob.GameEntity;
import edu.uob.Location;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class GameModel {
    private String startingLocation;
    private String currentPlayer;

    private HashMap<String, Location> locationList = new HashMap<>();

    public GameModel(File entitiesFileName, File actionsFileName) throws IOException, ParserConfigurationException, SAXException, ParseException {
        loadEntitiesFile(entitiesFileName.getAbsolutePath());
        loadActionsFile(actionsFileName.getAbsolutePath());
    }

    private void loadEntitiesFile(String entitiesFileName) throws FileNotFoundException, ParseException {
        Parser parser = new Parser();
        FileReader reader = new FileReader(entitiesFileName);
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();

        loadLocations(sections);
        loadPaths(sections);
    }

    private void loadLocations(ArrayList<Graph> sections){
        //TODO load location
        // The locations will always be in the first subgraph
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();

        // The starting location is the first location in entitiesFile
        this.startingLocation = locations.get(0).getNodes(false).get(0).getId().getId();

        for(Graph location : locations){
            Node locationDetails = location.getNodes(false).get(0);
            String locationName = locationDetails.getId().getId().toLowerCase();
            Location newLocation = new Location(locationDetails.getAttribute("description"));
            ArrayList<Graph> entities = location.getSubgraphs();

            // Load entities of each location
            for(Graph entity : entities){
                ArrayList<Node> entityNodes = entity.getNodes(false);
                String type = entity.getId().getId();
                for (Node node : entityNodes) {
                    GameEntity newEntity = new GameEntity(node.getId().getId(), node.getAttribute("description"), type);
                    newLocation.addEntity(newEntity);
                }
            }
            locationList.put(locationName, newLocation);
        }
    }

    private void loadPaths(ArrayList<Graph> sections)  {
    }

    private void loadActionsFile(String actionsFileName) throws ParserConfigurationException, IOException, SAXException{
    }

    public void setCurrentPlayer(String currentPlayerName){
        this.currentPlayer = currentPlayerName;
    }

    //TODO public Player getPlayerByName(String playerName)

    //TODO public HashMap<String, Player> getPlayerList()

    //TODO public TreeMap<String, HashSet<GameAction>> getActionList()

    //TODO public HashMap<String, Location> getLocationList()


}
