package edu.uob.configFileReader;

import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.util.List;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import edu.uob.GameEntities.*;


public class DotReader {
    private String startingLocation;
    private List<Location> locationsInEntitiesfile;
    private List<PathPair> pathsInEntitiesfile;

    public DotReader(File entitiesFile) throws ParseException, FileNotFoundException {
        Parser parser = new Parser();
        FileReader reader = new FileReader(entitiesFile);
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();
    }

    private void loadLocationsFromDot(ArrayList<Graph> sections){
        // The locations will always be in the first subgraph
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();

        // The starting location is the first location in entitiesFile
        this.startingLocation = locations.get(0).getNodes(false).get(0).getId().getId();


    }

    private void loadPathsFromDot(ArrayList<Graph> sections){
        // The paths will always be in the second subgraph
        ArrayList<Edge> paths = sections.get(1).getEdges();

        // Store the path pair into the pathlist
        for (Edge edge : paths){
            String sourceName = edge.getSource().getNode().getId().getId();
            String targetName = edge.getTarget().getNode().getId().getId();
            pathsInEntitiesfile.add(new PathPair(sourceName,targetName));
        }
    }

    public List<Location> getLocationList() {
        return this.locationsInEntitiesfile;
    }

    public String getStartingLocation() {
        return this.startingLocation;
    }

    public List<PathPair> getPathsInConfig(){
        return this.pathsInEntitiesfile;
    }

}
