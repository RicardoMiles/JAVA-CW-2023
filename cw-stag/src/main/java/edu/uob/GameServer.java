package edu.uob;

import com.alexmerz.graphviz.ParseException;
import edu.uob.GameEngine.CommandNormalizer;
import edu.uob.GameEngine.GameState;
import edu.uob.GameEntities.Location;
import edu.uob.GameEntities.PathPair;
import edu.uob.configFileReader.DotReader;
import edu.uob.configFileReader.XmlReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    public GameState currGameState;


    public static void main(String[] args) throws IOException, ParseException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Instanciates a new server instance, specifying a game with some configuration files
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    */
    public GameServer(File entitiesFile, File actionsFile) throws FileNotFoundException, ParseException {
        // TODO implement your server logic here
        // Read the entitiesFile into DotReader and export to GameState
        DotReader dotReader = new DotReader(entitiesFile);
        List<Location> locations = dotReader.locationsInEntitiesfile;
        String startingLocation = dotReader.startingLocation;
        HashMap<String,Location> currGameMap = dotReader.getGameMap();
        // Read the actionsFile into XmlReader and export to GameState
        XmlReader xmlReader = new XmlReader(actionsFile);
        HashMap<String, HashSet<GameAction>> currGameActions = xmlReader.getGameActions();
        this.currGameState = new GameState(startingLocation);
        currGameState.loadGameMap(currGameMap);
        currGameState.loadGameActions(currGameActions);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        CommandNormalizer cmdHandler =  new CommandNormalizer(command,currGameState);
        String currPlayer = cmdHandler.outputPlayerName();
        currGameState.loadPlayer(currPlayer);
        cmdHandler.importGameMap(currGameState.getCurrGameMap());
        String matchedCommand = cmdHandler.outputMatchedCommand();
        List<String> commandParts = cmdHandler.getCommandParts();
        String currLocationName = currGameState.locatePlayer(currPlayer).getName();
        boolean containExtraneousLocation = false;
        for (String part : commandParts) {
            if (!part.equals(currLocationName) && currGameState.getCurrGameMap().containsKey(part)) {
                containExtraneousLocation = true; // Find locationName other than currLocationName
            }
        }
        switch(matchedCommand.toLowerCase()){
            case "get":
                String itemToBePickedUp = cmdHandler.checkItemsToBeTaken();
                if(containExtraneousLocation){
                    return "Extraneous location in command. " + System.lineSeparator();
                }
                switch(itemToBePickedUp){
                    case "NoItem":
                        return "No valid item to pick up." + System.lineSeparator();
                    case "MultipleItem":
                        return "There is more than one thing you can get here - which one do you want" + System.lineSeparator();
                    case "Player not found in any location!":
                        return "Player not found in any location!"+ System.lineSeparator();
                    default:
                        return currGameState.getCMD(itemToBePickedUp,currPlayer);
                }
            case "drop":
                String itemToDrop = cmdHandler.checkItemToDrop(currPlayer);
                if(containExtraneousLocation){
                    return "Extraneous location in command. " + System.lineSeparator();
                }
                switch(itemToDrop){
                    case "NoItem":
                        return "No valid item to drop." + System.lineSeparator();
                    case "MultipleItem":
                        return "There is more than one thing you can drop here - which one do you want" + System.lineSeparator();
                    case "Player not found in any location!":
                        return "Player not found in any location!"+ System.lineSeparator();
                    default:
                        return currGameState.dropCMD(itemToDrop,currPlayer);
                }
            case "goto":
                String targetLocation = cmdHandler.findGotoTarget();
                boolean accessibleOrNot = currGameState.checkLocationAccessiblity(currPlayer,targetLocation);
                if(accessibleOrNot){
                    currGameState.gotoCMD(currPlayer,targetLocation);
                    return "You went to " + targetLocation + "." + System.lineSeparator();
                }else{
                    return "Goto Command Detected, but target location is invalid." + System.lineSeparator();
                }
            case "look":
                return currGameState.lookCMD(currPlayer);
            case "inventory":
            case "inv":
                return currGameState.inventoryCMD(currPlayer);
            case "health":
                int healthValue = currGameState.getPlayerHealth(currPlayer);
                String convertedHealth = Integer.toString(healthValue);
                return convertedHealth;
            case "conflicted unsupported multiple command":
                return "there is more than one valid action possible - which one do you want to perform ?";
            case "no matched command":{
                return "Unknown command ! ! ! ";
            }
            default:
                Set<GameAction> actions = currGameState.getActionsForTrigger(matchedCommand);
                if (!actions.isEmpty()) {
                    for (GameAction action : actions) {
                        Set<String> subjects = action.getSubjects();
                        Set<String> commandEntities = new HashSet<>(commandParts);
                        commandEntities.retainAll(subjects);

                        if (commandEntities.size() > subjects.size()) {
                            return "Extraneous Entities for" + matchedCommand + System.lineSeparator();
                        }
                        if (commandEntities.size() < 1) {
                            return "All subjects missing for action" + matchedCommand + System.lineSeparator();
                        }
                        if (commandEntities.size() <= subjects.size()) {
                            // Further check for subjects
                            if (currGameState.checkPlayerAndLocationHaveAllEntities(currPlayer, subjects)) {
                                // Execute the action for producing and consuming entities
                                currGameState.performActionFromFiles(action, currPlayer);
                                if (currGameState.getPlayerHealth(currPlayer) == 0) {
                                    currGameState.dropAllItems(currPlayer);
                                    currGameState.findPlayerByName(currPlayer).resetHealth();
                                    currGameState.gotoCMD(currPlayer, currGameState.getStartingLocation());
                                    return "you died and lost all of your items, you must return to the start of the game" + System.lineSeparator();
                                }
                                return action.getNarration() + System.lineSeparator();
                            } else {
                                return "Player or Location missing required entities for action: " + System.lineSeparator();
                            }
                        }


                    }
                    return "Action performed: " + matchedCommand;
                }
                return "Flexible command detected, but execution failed. " + System.lineSeparator();
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Starts a *blocking* socket server listening for new connections.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
