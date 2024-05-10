package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test class which runs a large number of gameplay tests based on an
 * extended actions file
 */
class ExtendedTests {
    /**
     * server object through which the tests are run
     */
    private GameServer server;

    @BeforeEach
    void setup() {
        final File entitiesFile = Paths.get("config" + File.separator + "extended" +
                "-entities.dot").toAbsolutePath().toFile();
        final File actionsFile = Paths.get("config" + File.separator + "extended" +
                "-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    private String sendCommandToServer(final String command) {
        // Try to send a command to the server - this call will time out if
        // it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(100000), () -> server.handleCommand(command),
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void test1Step1() {
        final String response1 = sendCommandToServer("test: goto forest");
        assertTrue(response1.contains("cabin"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step2() {
        sendCommandToServer("test: goto FOREST");
        final String response2 = sendCommandToServer("test: goto forest");
        assertTrue(response2.contains("Error"),
                "cannot go to location you are already in");
    }

    @Test
    void test1Step3() {
        sendCommandToServer("test: goto forest");
        final String response3 = sendCommandToServer("test: get key");
        assertTrue(response3.contains("picked up"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step4() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        final String response4 = sendCommandToServer("test: goto cabin");
        assertTrue(response4.contains("cabin"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step5() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        sendCommandToServer("test: goto cabin");
        final String response5 = sendCommandToServer("test: open key");
        assertTrue(response5.contains("unlock"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step6() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        sendCommandToServer("test: goto cabin");
        sendCommandToServer("test: open key");
        final String response6 = sendCommandToServer("test: goto cellar");
        assertTrue(response6.contains("elf"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step7() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        sendCommandToServer("test: goto cabin");
        sendCommandToServer("test: open key");
        sendCommandToServer("test: goto cellar");
        final String response7 = sendCommandToServer("test: health");
        assertTrue(response7.contains("3"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step8() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        sendCommandToServer("test: goto cabin");
        sendCommandToServer("test: open key");
        sendCommandToServer("test: goto cellar");
        final String response8 = sendCommandToServer("test: hit elf");
        assertTrue(response8.contains("lose some health"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step9() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        sendCommandToServer("test: goto cabin");
        sendCommandToServer("test: open key");
        sendCommandToServer("test: goto cellar");
        sendCommandToServer("test: hit elf");
        final String response9 = sendCommandToServer("test: health");
        assertTrue(response9.contains("2"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step10() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        sendCommandToServer("test: goto cabin");
        sendCommandToServer("test: open key");
        sendCommandToServer("test: goto cellar");
        sendCommandToServer("test: hit elf");
        final String response10 = sendCommandToServer("test: hit elf");
        assertTrue(response10.contains("lose some health"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step11() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        sendCommandToServer("test: goto cabin");
        sendCommandToServer("test: open key");
        sendCommandToServer("test: goto cellar");
        sendCommandToServer("test: hit elf");
        sendCommandToServer("test: hit elf");
        final String response11 = sendCommandToServer("test: health");
        assertTrue(response11.contains("1"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step12() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        sendCommandToServer("test: goto cabin");
        sendCommandToServer("test: open key");
        sendCommandToServer("test: goto cellar");
        sendCommandToServer("test: hit elf");
        sendCommandToServer("test: hit elf");
        final String response12 = sendCommandToServer("test: hit elf");
        assertTrue(response12.contains("you died"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step13() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        sendCommandToServer("test: goto cabin");
        sendCommandToServer("test: open key");
        sendCommandToServer("test: goto cellar");
        sendCommandToServer("test: hit elf");
        sendCommandToServer("test: hit elf");
        sendCommandToServer("test: hit elf");
        final String response13 = sendCommandToServer("test: health");
        assertTrue(response13.contains("3"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void test1Step14() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        sendCommandToServer("test: goto cabin");
        sendCommandToServer("test: open key");
        sendCommandToServer("test: goto cellar");
        sendCommandToServer("test: hit elf");
        sendCommandToServer("test: hit elf");
        sendCommandToServer("test: hit elf");
        final String response14 = sendCommandToServer("test: inv");
        assertTrue(response14.contains("empty"),
                "normal play-through of game should behave as expected");
    }

    @Test
    void extraEntityTest() {
        sendCommandToServer("test: goto forest");
        sendCommandToServer("test: get key");
        sendCommandToServer("test: goto cabin");
        final String response5 = sendCommandToServer("test: open trapdoor with key " +
                "and potion");
        assertTrue(response5.contains("Error"),
                "cannot have an entity name as decoration");
    }

    @Test
    void testHealing() {
        sendCommandToServer("Alex: get potion");
        sendCommandToServer("Alex: goto forest");
        sendCommandToServer("Alex: get key");
        sendCommandToServer("Alex: goto cabin");
        sendCommandToServer("Alex: open key");
        sendCommandToServer("Alex: goto cellar");
        sendCommandToServer("Alex: hit elf");
        sendCommandToServer("Alex: hit elf");
        sendCommandToServer("Alex: drink potion");
        final String response10 = sendCommandToServer("Alex: health");
        assertTrue(response10.contains("2"),
                "drinking a potion should increase player's health (provided it " +
                        "was below 3 before drinking");
    }

    @Test
    void testDeath() {
        sendCommandToServer("Alex: get axe");
        sendCommandToServer("Alex: get potion");
        sendCommandToServer("Alex: goto forest");
        sendCommandToServer("Alex: get key");
        sendCommandToServer("Alex: goto cabin");
        sendCommandToServer("Alex: open key");
        sendCommandToServer("Alex: goto cellar");
        sendCommandToServer("Alex: hit elf");
        sendCommandToServer("Alex: hit elf");
        sendCommandToServer("Alex: hit elf");
        final String response10 = sendCommandToServer("Alex: look");
        assertTrue(response10.contains("A log cabin in the woods"),
                "dying should send the player back to the start location");
    }

    @Test
    void testCallingLumberjack() {
        sendCommandToServer("Alex: goto forest");
        sendCommandToServer("Alex: goto riverbank");
        sendCommandToServer("Alex: get horn");
        final String response4 = sendCommandToServer("Alex: blow horn");
        assertTrue( response4.contains("You blow the horn and as if by magic, a lumberjack "),
                "normal play-through of game should behave as expected");
    }

    @Test
    void testCuttingTree() {
        sendCommandToServer("Alex: get axe");
        sendCommandToServer("Alex: goto forest");
        final String response3 = sendCommandToServer("Alex: cut tree");
        assertTrue(response3.contains("You cut down the tree with the axe") ,
                "normal play-through of game should behave as expected");
    }

    @Test
    void testGrowingTree() {
        sendCommandToServer("Alex: get axe");
        sendCommandToServer("Alex: goto forest");
        sendCommandToServer("Alex: cut tree");
        final String response4 = sendCommandToServer("Alex: grow seed");
        assertTrue(response4.contains("You grow a tree"),
                "actions that consume nothing should be handled properly");
    }

//    @Test
//    void testProducingArtefactFromCharacter() {
//        sendCommandToServer("Alex: get axe");
//        sendCommandToServer("Alex: goto forest");
//        sendCommandToServer("Alex: cut tree");
//        sendCommandToServer("Alex: get log");
//        sendCommandToServer("Alex: grow seed");
//        sendCommandToServer("Alex: cut tree");
//        final String response8 = sendCommandToServer("Alex: look");
//        assertEquals("You are in A deep dark forest You can see:\n" +
//                        "key: A rusty old key\n" +
//                        "log: A heavy wooden log\n" +
//                        "You can see from here:\n" +
//                        "cabin\n" +
//                        "riverbank\n", response8,
//                "producing an artefact that is held by a player should take it " +
//                        "from the player's inventory and bring it to the location");
//    }

    @Test
    void testBurningBridge() {
        sendCommandToServer("Alex: get axe");
        sendCommandToServer("Alex: goto forest");
        sendCommandToServer("Alex: cut tree");
        sendCommandToServer("Alex: get log");
        sendCommandToServer("Alex: goto riverbank");
        sendCommandToServer("Alex: bridge river");
        final String response7 = sendCommandToServer("Alex: burn down route from " +
                "riverbank");
        assertTrue(response7.contains("You burn down the bridge"),
                "actions that remove a path should be handled properly");
    }

    @Test
    void testSinging() {
        final String response1 = sendCommandToServer("Alex: sing");
        assertTrue(response1.contains("You sing a sweet song"),
                "actions that have no subjects, produced, or consumed should be " +
                        "handled properly");
    }

    @Test
    void testInvalidAction() {
        final String response1 = sendCommandToServer("Alex: blow horn");
        assertTrue(response1.contains("Error"),
                "cannot perform an action if not all of the subjects are present");
    }

    @Test
    void testDrinkPotion() {
        final String response1 = sendCommandToServer("Alex: drink potion");
        assertTrue(response1.contains("You drink the potion and your health improves health: 4"),
                "should be able to use entities in the location as subjects");
    }

    @Test
    void testDrinkPotionUpper() {
        final String response1 = sendCommandToServer("Alex: DRINK potion");
        assertTrue(response1.contains("You drink the potion and your health improves health: 4"),
                "should be able to use entities in the location as subjects");
    }

    @Test
    void testAmbiguousMultiWordTriggers() {
        final String response1 = sendCommandToServer("Alex: make music");
        assertTrue(response1.contains("Error"),
                "multi-word trigger that could do two actions is not valid");
    }

    @Test
    void testAmbiguousSingleWordTriggers() {
        final String response1 = sendCommandToServer("Alex: random");
        assertTrue(response1.contains("Error"),
                "multi-word trigger that could do two actions is not valid");
    }

    @Test
    void testRemovingOneOfManyCharacters() {
        sendCommandToServer("Ollie: goto forest");
        sendCommandToServer("Ollie: get key");
        sendCommandToServer("Ollie: goto riverbank");
        sendCommandToServer("Ollie: get horn");
        sendCommandToServer("Ollie: goto forest");
        sendCommandToServer("Ollie: goto cabin");
        sendCommandToServer("Ollie: open trapdoor");
        sendCommandToServer("Ollie: goto cellar");
        sendCommandToServer("Ollie: blow horn");
        sendCommandToServer("Ollie: goto cabin");
        final String response1 = sendCommandToServer("Ollie: blow horn");
        assertTrue(response1.contains("You blow the horn and as if by magic"),
                "should be able to remove one character from a location with more" +
                        " than one character");
    }

    @Test
    void testNotAmbiguousSharedtrigger() {
        sendCommandToServer("Kate: goto forest");
        sendCommandToServer("Kate: get key");
        sendCommandToServer("Kate: goto cabin");
        final String response1 = sendCommandToServer("Kate: use key");
        assertTrue(response1.contains("unlock"),
                "where a trigger relates to multiple actions, but only one is " +
                        "doable, the doable one should be performed");
    }

    @Test
    void testDoubleTrigger() {
        sendCommandToServer("Chris: get axe");
        sendCommandToServer("Chris: goto forest");
        final String response1 = sendCommandToServer("Chris: chop cut tree");
        assertTrue(response1.contains("You cut down the tree with the axe"),
                "can provide more than one valid trigger for an action");
    }

    @Test
    void testDoubleTriggerAllUpper() {
        sendCommandToServer("Chris: get AXE");
        sendCommandToServer("Chris: goto FOREST");
        final String response1 = sendCommandToServer("Chris: chop cut TREE");
        assertTrue(response1.contains("You cut down the tree with the axe"),
                "can provide more than one valid trigger for an action");
    }

    @Test
    void testDoubleTriggerUpper() {
        sendCommandToServer("Chris: GET axe");
        sendCommandToServer("Chris: GOTO forest");
        final String response1 = sendCommandToServer("Chris: CHOP CUT tree");
        assertTrue(response1.contains("You cut down the tree with the axe"),
                "can provide more than one valid trigger for an action");
    }

    @Test
    void testAmbiguousMultiWordTrigger() {
        final String response1 = sendCommandToServer("Gus: make music");
        assertTrue(response1.contains("Error"),
                "multi-word triggers that are ambiguous should not work");
    }

    @Test
    void testCommandActionCombo() {
        final String response1 = sendCommandToServer("Jake: look for potion to " +
                "drink");
        assertTrue(response1.contains("Error"),
                "reserved word cannot be decoration for action");
    }

    @Test
    void testImpossibleMultiWordTrigger() {
        final String response1 = sendCommandToServer("Sion: flirt with lumberjack");
        assertTrue(response1.contains("Error"),
                "reserved word cannot be decoration for action");
    }

    @Test
    void testNoSubjectMentioned() {
        final String response1 = sendCommandToServer("Cesca: drink");
        assertTrue(response1.contains("Error"),
                "reserved word cannot be decoration for action");
    }

    @Test
    void testConsumedButNotSubject() {
        final String response1 = sendCommandToServer("Eamon: pointless");
        assertTrue(response1.contains("You blow the horn and as if by magic, a lumberjack " +
                        "appears !"),
                "action with no subjects can still consume an entity");
    }

    @Test
    void testImpossibleSubject() {
        final String response1 = sendCommandToServer("Bala: boomerang");
        assertTrue(response1.contains("Error"),
                "reserved word cannot be decoration for action");
    }

    @Test
    void testImpossibleConsumed() {
        final String response1 = sendCommandToServer("Harry: bottle");
        assertTrue( response1.contains("This shouldn't be possible"), "Should not be able to perform this " +
                "action as consumeddoes not exist in the game");
    }

    @Test
    void testImpossibleProduced() {
        final String response1 = sendCommandToServer("Ed: MacBook");
        assertTrue(response1.contains("Error"),
                "reserved word cannot be decoration for action");
    }
}