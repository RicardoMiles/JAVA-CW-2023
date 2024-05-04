package edu.uob.GameEngine;

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
    private String currentPlayer;

    public GameModel(File entitiesFileName, File actionsFileName) throws IOException, ParserConfigurationException, SAXException{
        try {
            loadEntitiesFile(entitiesFileName.getAbsolutePath());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        loadActionsFile(actionsFileName.getAbsolutePath());
    }

    private void loadEntitiesFile(String entitiesFileName) throws FileNotFoundException, ParseException{
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
