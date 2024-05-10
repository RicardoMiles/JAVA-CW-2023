package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class BasicCommandTests {

  private GameServer server;

  // Make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() throws IOException, ParseException, ParserConfigurationException, SAXException {
      File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  // Test to spawn a new server and send a simple "look" command
  @Test
  void testLookingAroundStartLocation() {
    String response = server.handleCommand("Simon: look").toLowerCase();
    assertTrue(response.contains("a log cabin in the woods"), "Did not see description of room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
  }

  @Test
  void testCommandWithSymbols(){
    String response = server.handleCommand("Simon: look:").toLowerCase();
    assertTrue(response.contains("a log cabin in the woods"), "Did not see description of room in response to look");
    String response2 = server.handleCommand("Simon: -look-").toLowerCase();
    assertTrue(response2.contains("a log cabin in the woods"), "Did not see description of room in response to look");

    String response3 = server.handleCommand("Simon: cut -down- the tree").toLowerCase();
    assertFalse(response3.contains("a log cabin in the woods"), "Did not see description of room in response to look");
  }

  @Test
  void testPartialCommands1(){
    String response1 = server.handleCommand("Simon: goto forest").toLowerCase();
    assertTrue(response1.contains("went to forest"), "Did not see description of action in response to goto");

    String response2 = server.handleCommand("Simon: get key").toLowerCase();
    assertTrue(response2.contains("picked up a key"), "Did not see description of room in response to get");

    String response3 = server.handleCommand("Simon: goto cabin").toLowerCase();
    assertTrue(response3.contains("went to cabin"), "Did not see description of action in response to goto");

    // Full command
    String response4 = server.handleCommand("Simon: open the trapdoor with key").toLowerCase();
    assertTrue(response4.contains("unlock the trapdoor"), "Did not see description of room in response to unlock");

    String response5 = server.handleCommand("Simon: goto cellar").toLowerCase();
    assertTrue(response5.contains("went to cellar"), "Did not see description of action in response to goto");
  }

  @Test
  void testPartialCommands2(){
    String response1 = server.handleCommand("Simon: goto forest").toLowerCase();
    assertTrue(response1.contains("went to forest"), "Did not see description of action in response to goto");

    String response2 = server.handleCommand("Simon: get key").toLowerCase();
    assertTrue(response2.contains("picked up a key"), "Did not see description of room in response to get");

    String response3 = server.handleCommand("Simon: goto cabin").toLowerCase();
    assertTrue(response3.contains("went to cabin"), "Did not see description of action in response to goto");

    // Command with no subject
    String response4 = server.handleCommand("Simon: open").toLowerCase();
    assertTrue(response4.contains("sorry"), "This command is ambiguous, it should not be executed");

    // Command with only one subject - trapdoor
    String response5 = server.handleCommand("Simon: open the trapdoor").toLowerCase();
    assertTrue(response5.contains("unlock the trapdoor"), "Did not see description of room in response to unlock");

    String response6 = server.handleCommand("Simon: goto cellar").toLowerCase();
    assertTrue(response6.contains("went to cellar"), "Did not see description of action in response to goto");
  }

  @Test
  void testPartialCommands3(){
    String response1 = server.handleCommand("Simon: goto forest").toLowerCase();
    assertTrue(response1.contains("went to forest"), "Did not see description of action in response to goto");

    String response2 = server.handleCommand("Simon: get key").toLowerCase();
    assertTrue(response2.contains("picked up a key"), "Did not see description of room in response to get");

    String response3 = server.handleCommand("Simon: goto cabin").toLowerCase();
    assertTrue(response3.contains("went to cabin"), "Did not see description of action in response to goto");

    // Command with only one subject - key
    String response4 = server.handleCommand("Simon: unlock with key").toLowerCase();
    assertTrue(response4.contains("unlock the trapdoor"), "Did not see description of room in response to unlock");

    String response5 = server.handleCommand("Simon: goto cellar").toLowerCase();
    assertTrue(response5.contains("went to cellar"), "Did not see description of action in response to goto");
  }

  @Test
  void testIntegrationCommands(){
    String response = server.handleCommand("Simon: look").toLowerCase();
    assertTrue(response.contains("a log cabin in the woods"), "Did not see description of room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");

    String response2 = server.handleCommand("Simon: get potion").toLowerCase();
    assertTrue(response2.contains("picked up a potion"), "Did not see description of room in response to get");

    String response3 = server.handleCommand("Simon: goto forest").toLowerCase();
    assertTrue(response3.contains("went to forest"), "Did not see description of action in response to goto");

    String response4 = server.handleCommand("Simon: look").toLowerCase();
    assertTrue(response4.contains("dark forest"), "Did not see description of room in response to look");
    assertTrue(response4.contains("key"), "Did not see description of room in response to look");
    assertTrue(response4.contains("cabin"), "Did not see description of room in response to look");

    String response5 = server.handleCommand("Simon: get key").toLowerCase();
    assertTrue(response5.contains("picked up a key"), "Did not see description of room in response to get");

    String response6 = server.handleCommand("Simon: goto cabin").toLowerCase();
    assertTrue(response6.contains("went to cabin"), "Did not see description of action in response to goto");

    String response7 = server.handleCommand("Simon: open the trapdoor").toLowerCase();
    assertTrue(response7.contains("unlock the trapdoor"), "Did not see description of room in response to unlock");

    String response8 = server.handleCommand("Simon: goto cellar").toLowerCase();
    assertTrue(response8.contains("went to cellar"), "Did not see description of action in response to goto");

    String response9 = server.handleCommand("Simon: look").toLowerCase();
    assertTrue(response9.contains("dusty cellar"), "Did not see description of room in response to look");
    assertTrue(response9.contains("elf"), "Did not see description of room in response to look");

    String response10 = server.handleCommand("Simon: hit elf").toLowerCase();
    assertTrue(response10.contains("lose some health"), "Did not see description of room in response to hit");

    String response11 = server.handleCommand("Simon: hit elf").toLowerCase();
    assertTrue(response11.contains("lose some health"), "Did not see description of room in response to hit");

    String response12 = server.handleCommand("Simon: drink potion").toLowerCase();
    assertTrue(response12.contains("health improves"), "Did not see description of room in response to drink");

    String response13 = server.handleCommand("Simon: hit elf").toLowerCase();
    assertTrue(response13.contains("lose some health"), "Did not see description of room in response to hit");

    String response14 = server.handleCommand("Simon: hit elf").toLowerCase();
    assertTrue(response14.contains("you died"), "Did not see description of room in response to hit");

    String response15 = server.handleCommand("Simon: look").toLowerCase();
    assertTrue(response15.contains("a log cabin in the woods"), "Did not see description of room in response to look");

    String response16 = server.handleCommand("Simon: cut the tree").toLowerCase();
    assertTrue(response16.contains("sorry"), "Did not see description of room in response to cut");
  }

}
