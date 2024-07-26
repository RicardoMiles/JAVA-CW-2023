package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.Duration;

class TestForFlexibleCommands {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }
    
    // Add more unit tests or integration tests here.
    @Test
    void testExtraneousEntities(){
        String response;
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("potion") && response.contains("axe"), "Did not see the potion and the axe in the inventory after an attempt was made to get them");
        response = sendCommandToServer("simon: drop axe and potion");
        response = response.toLowerCase();
        assertTrue(response.contains("extraneous") || response.contains("there is more than one thing you can drop here - which one do you want"),"Extraneous entity exclude feature failure in drop axe and extraneous entity.");
    }

    @Test
    void testExtraneousEntitiesInDLC(){
        String response;
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon: get coin");
        response = sendCommandToServer("simon: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("potion") && response.contains("axe") && response.contains("coin"), "Did not see the potion, the axe and the coin in the inventory after an attempt was made to get them");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: cut tree");
        response = response.toLowerCase();
        assertTrue(response.contains("you cut down the tree with the axe"),"Cut the tree failed! ");
        sendCommandToServer("simon: get log");
        response = sendCommandToServer("simon: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("log"),"Producing after cut action failed.");
    }

    @Test
    void testDeathMechanismOnMultiplayer(){
        String response;
        sendCommandToServer("simon: look");
        response = sendCommandToServer("kobe: look");
        response = response.toLowerCase();
        assertTrue(response.contains("simon"),"Fully error on multiplayer visibility. ");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("kobe"),"Single visibility on Multiplayer mode");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open trapdoor");
        response = sendCommandToServer("kobe: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cellar"),"Sync feature for multiplayer failure.");
        sendCommandToServer("kobe: goto cellar");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: hit the elf");
        sendCommandToServer("simon: hit the elf");
        sendCommandToServer("simon: hit the elf");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"),"Reload player to born place fail! ");
        response = sendCommandToServer("kobe: look");
        response = response.toLowerCase();
        assertTrue(!response.contains("simon") && response.contains("axe") && response.contains("coin") && response.contains("potion"),"Death mechanism failed, dead player stay at current place, or dead player's inv items missing.");
    }

    @Test
    void testHealthfeature(){
        String response;
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open trapdoor");
        sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: health");
        response = response.toLowerCase();
        assertTrue(response.contains("3"),"Health query failed.");
        sendCommandToServer("simon: hit the elf");
        response = sendCommandToServer("simon: health");
        response = response.toLowerCase();
        assertTrue(response.contains("2"),"Health decrease error.");
        sendCommandToServer("simon: hit the elf");
        sendCommandToServer("simon: drink potion");
        response = sendCommandToServer("simon: health");
        response = response.toLowerCase();
        assertTrue(response.contains("2"),"Health increase error.");
    }

    @Test
    void testDigGold(){
        String response;
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open trapdoor");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: pay the elf");
        sendCommandToServer("simon: get the shovel");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: cut tree");
        sendCommandToServer("simon: get log");
        sendCommandToServer("simon: goto riverbank");
        sendCommandToServer("simon: bridge river");
        sendCommandToServer("simon: goto clearing");
        sendCommandToServer("simon: dig ground");
        sendCommandToServer("simon: get gold");
        response = sendCommandToServer("simon: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("gold"),"Gold achievement failure");
    }
}
