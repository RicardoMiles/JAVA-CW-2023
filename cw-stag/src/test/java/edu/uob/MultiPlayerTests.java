package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test class for ensuring that multiplayer functionality is as desired
 */
class MultiPlayerTests {
    /**
     * server object through which the tests are run
     */
    private GameServer server;

    @BeforeEach
    void setup() throws IOException {
        final File entitiesFile = Paths.get("config" + File.separator + "extended" +
                "-entities.dot").toAbsolutePath().toFile();
        final File actionsFile = Paths.get("config" + File.separator + "extended" +
                "-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    private String sendCommandToServer(final String command) {
        // Try to send a command to the server - this call will timeout if it
        // takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testTwoPlayersSimple1() {
        final String sionResponse1 = sendCommandToServer("Sion: look");
        assertEquals("you are in cabin-A log cabin in the woods\n" +
                        "You can see artefacts :\n" +
                        "potion - A bottle of magic potion\n" +
                        "axe - A razor sharp axe\n" +
                        "coin - A silver coin\n" +
                        "You can see Furniture :\n" +
                        "trapdoor - A locked wooden trapdoor in the floor\n" +
                        "You can access from here:\n" +
                        "forest\n", sionResponse1,
                "commands should work from one user");
    }

    @Test
    void testTwoPlayersSimple2() {
        sendCommandToServer("Sion: look");
        final String simonResponse1 = sendCommandToServer("Sion: look");
        assertEquals("you are in cabin-A log cabin in the woods\n" +
                "You can see artefacts :\n" +
                "potion - A bottle of magic potion\n" +
                "axe - A razor sharp axe\n" +
                "coin - A silver coin\n" +
                "You can see Furniture :\n" +
                "trapdoor - A locked wooden trapdoor in the floor\n" +
                "You can access from here:\n" +
                "forest\n" +
                "You can see player :\n" +
                "Sion\n", simonResponse1, "should be able to see other player " +
                "in the same location as you");
    }
}