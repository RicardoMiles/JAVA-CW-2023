package edu.uob.ConfigFilesReader;

import edu.uob.GameAction;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class XmlReader {

    private HashMap<String, HashSet<GameAction>> actionList;

    public XmlReader(File actionFile) throws ParserConfigurationException, IOException, SAXException {
        actionList = new HashMap<>();
        parseXmlFile(actionFile);
    }

    private void parseXmlFile(File actionsFile) {
        DocumentBuilder builder = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = null;
        try {
            document = builder.parse(actionsFile);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Element root = document.getDocumentElement();
        NodeList actionNodes = root.getElementsByTagName("action");

        for (int i = 0; i < actionNodes.getLength(); i++) {
            Element actionElement = (Element) actionNodes.item(i);
            GameAction gameAction = parseActionElement(actionElement);

            // Save gameActions to HashMap
            for (String triggerPhrase : gameAction.getTriggerPhrases()) {
                actionList.computeIfAbsent(triggerPhrase, k -> new HashSet<>()).add(gameAction);
            }
        }
    }

    /**
     * Parses the given XML element and creates a corresponding GameAction object.
     * This method extracts the trigger phrases, subject entities, consumed entities,
     * produced entities, and narration defined within the actionElement.
     *
     * @param actionElement an XML element contains game actions DLC definition
     * @return GameAction object set according to XML element
     */
    private GameAction parseActionElement(Element actionElement) {
        // Parse trigger phrases
        Set<String> triggerPhrases = new HashSet<>();
        NodeList triggerNodes = actionElement.getElementsByTagName("keyphrase");
        for (int j = 0; j < triggerNodes.getLength(); j++) {
            triggerPhrases.add(triggerNodes.item(j).getTextContent());
        }

        // Parse subject entities
        Set<String> subjects = new HashSet<>();
        NodeList subjectNodes = actionElement.getElementsByTagName("subjects").item(0).getChildNodes();
        for (int j = 0; j < subjectNodes.getLength(); j++) {
            if (subjectNodes.item(j) instanceof Element) {
                subjects.add(subjectNodes.item(j).getTextContent());
            }
        }

        // Parse consumed entities
        Set<String> consumedEntities = new HashSet<>();
        Element consumedElement = (Element) actionElement.getElementsByTagName("consumed").item(0);
        if (consumedElement != null) {
            NodeList consumedNodes = consumedElement.getElementsByTagName("entity");
            for (int j = 0; j < consumedNodes.getLength(); j++) {
                consumedEntities.add(consumedNodes.item(j).getTextContent());
            }
        }

        // Parse produced entities
        Set<String> producedEntities = new HashSet<>();
        Element producedElement = (Element) actionElement.getElementsByTagName("produced").item(0);
        if (producedElement != null) {
            NodeList producedNodes = producedElement.getElementsByTagName("entity");
            for (int j = 0; j < producedNodes.getLength(); j++) {
                producedEntities.add(producedNodes.item(j).getTextContent());
            }
        }

        // Parse narration
        String narration = actionElement.getElementsByTagName("narration").item(0).getTextContent();

        // Create and return GameAction object
        return new GameAction(triggerPhrases, subjects, consumedEntities, producedEntities, narration);
    }

    public HashMap<String, HashSet<GameAction>> getGameActions() {
        return actionList;
    }

}
