package edu.uob.GameEngine;

import edu.uob.GameAction;
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

    private TreeMap<String, HashSet<GameAction>> actionList = new TreeMap<>();

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
        // The paths will always be in the second subgraph
        ArrayList<Edge> paths = sections.get(1).getEdges();
        for(Edge path : paths){
            Node fromLocation = path.getSource().getNode();
            String fromName = fromLocation.getId().getId().toLowerCase();
            Location fromLoc = locationList.get(fromName);
            Node toLocation = path.getTarget().getNode();
            String toName = toLocation.getId().getId().toLowerCase();
            if(!locationList.containsKey(fromName) || !locationList.containsKey(toName)){
                throw new RuntimeException("cannot create paths between two locations.\n");
            }
            fromLoc.addPath(toName);
        }
    }

    private void loadActionsFile(String actionsFileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(actionsFileName);
        Element root = document.getDocumentElement();
        NodeList actions = root.getChildNodes();

        // Get actions by index (only odd items are actions - 1,3,5 etc.)
        for(int i=1; i<actions.getLength(); i+=2){
            Element action = (Element)actions.item(i);
            GameAction newAction = new GameAction();
            Element triggers = (Element)action.getElementsByTagName("triggers").item(0);

            // Add attributes to newAction
            for(actionState type : actionState.values()) {
                Element currentType = (Element) action.getElementsByTagName(type.toString()).item(0);
                for(int k=0; k<currentType.getElementsByTagName("entity").getLength(); k++) {
                    String typePhrase = currentType.getElementsByTagName("entity").item(k).getTextContent();
                    newAction.addAttributes(type.toString(), typePhrase);
                }
            }

            // Add narration to newAction
            Element narration = (Element) action.getElementsByTagName("narration").item(0);
            String narrationSentence = narration.getTextContent();
            newAction.addNarration(narrationSentence);

            // Get trigger phrases
            for(int j=0; j<triggers.getElementsByTagName("keyword").getLength(); j++){
                String triggerPhrase = triggers.getElementsByTagName("keyword").item(j).getTextContent();
                // Check if the hashset of a trigger already exists
                if(actionList.containsKey(triggerPhrase)){
                    if(!actionExists(triggerPhrase, newAction)) {
                        actionList.get(triggerPhrase).add(newAction);
                        actionList.put(triggerPhrase, actionList.get(triggerPhrase));
                    }
                } else {
                    HashSet<GameAction> list = new HashSet<>();
                    list.add(newAction);
                    actionList.put(triggerPhrase, list);
                }
            }

        }

    }

    // Check if there are two identical actions in xml file
    private boolean actionExists(String triggerPhrase, GameAction newAction){
        HashSet<GameAction> actions = actionList.get(triggerPhrase);
        for(GameAction action : actions){
            if(action.getSubjects().equals(newAction.getSubjects()) || action.getNarration().equals(newAction.getNarration())
                    || action.getConsumed().equals(newAction.getConsumed()) || action.getProduced().equals(newAction.getProduced()))
                return true;
        }
        return false;
    }

    public void setCurrentPlayer(String currentPlayerName){
        this.currentPlayer = currentPlayerName;
    }

    //TODO public Player getPlayerByName(String playerName)

    //TODO public HashMap<String, Player> getPlayerList()

    //TODO public TreeMap<String, HashSet<GameAction>> getActionList()

    //TODO public HashMap<String, Location> getLocationList()


}
