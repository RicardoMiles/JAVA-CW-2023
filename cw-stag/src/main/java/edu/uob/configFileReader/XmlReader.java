package edu.uob.configFileReader;

import edu.uob.GameAction;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class XmlReader {

    private HashMap<String, HashSet<GameAction>> actionList;

    public XmlReader(File actionFile) {
        actionList = new HashMap<>();
        parseXmlFile(actionFile);
    }

    private void parseXmlFile(File actionsFile) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile);
            Element root = document.getDocumentElement();
            NodeList actionNodes = root.getElementsByTagName("action");

            for (int i = 0; i < actionNodes.getLength(); i++) {
                Element actionElement = (Element) actionNodes.item(i);
                GameAction gameAction = parseActionElement(actionElement);

                // 将动作存储到 HashMap 中
                for (String triggerPhrase : gameAction.getTriggerPhrases()) {
                    actionList.computeIfAbsent(triggerPhrase, k -> new HashSet<>()).add(gameAction);
                }
            }

            // 输出测试，打印所有解析的动作
            printActions();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GameAction parseActionElement(Element actionElement) {
        // 解析触发短语
        Set<String> triggerPhrases = new HashSet<>();
        NodeList triggerNodes = actionElement.getElementsByTagName("keyphrase");
        for (int j = 0; j < triggerNodes.getLength(); j++) {
            triggerPhrases.add(triggerNodes.item(j).getTextContent());
        }

        // 解析主体实体
        Set<String> subjects = new HashSet<>();
        NodeList subjectNodes = actionElement.getElementsByTagName("subjects").item(0).getChildNodes();
        for (int j = 0; j < subjectNodes.getLength(); j++) {
            if (subjectNodes.item(j) instanceof Element) {
                subjects.add(subjectNodes.item(j).getTextContent());
            }
        }

        // 解析消耗实体
        Set<String> consumedEntities = new HashSet<>();
        Element consumedElement = (Element) actionElement.getElementsByTagName("consumed").item(0);
        if (consumedElement != null) {
            NodeList consumedNodes = consumedElement.getElementsByTagName("entity");
            for (int j = 0; j < consumedNodes.getLength(); j++) {
                consumedEntities.add(consumedNodes.item(j).getTextContent());
            }
        }

        // 解析生成实体
        Set<String> producedEntities = new HashSet<>();
        Element producedElement = (Element) actionElement.getElementsByTagName("produced").item(0);
        if (producedElement != null) {
            NodeList producedNodes = producedElement.getElementsByTagName("entity");
            for (int j = 0; j < producedNodes.getLength(); j++) {
                producedEntities.add(producedNodes.item(j).getTextContent());
            }
        }

        // 解析叙述
        String narration = actionElement.getElementsByTagName("narration").item(0).getTextContent();

        // 创建 GameAction 对象
        return new GameAction(triggerPhrases, subjects, consumedEntities, producedEntities, narration);
    }

    private void printActions() {
        actionList.forEach((key, value) -> {
            System.out.println("Trigger Phrase: " + key);
            value.forEach(action -> {
                System.out.println("  Subjects: " + action.getSubjects());
                System.out.println("  Consumed Entities: " + action.getConsumedEntities());
                System.out.println("  Produced Entities: " + action.getProducedEntities());
                System.out.println("  Narration: " + action.getNarration());
            });
            System.out.println("----------------------------------------");
        });
    }

    public HashMap<String, HashSet<GameAction>> getGameActions() {
        return actionList;
    }

}
