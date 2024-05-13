package edu.uob.ConfigFileReader;

public class XmlReader {

//    void testBasicActionsFileIsReadable() {
//        try {
//            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//            Document document = builder.parse("config" + File.separator + "basic-actions.xml");
//            Element root = document.getDocumentElement();
//            NodeList actions = root.getChildNodes();
//            // Get the first action (only the odd items are actually actions - 1, 3, 5 etc.)
//            Element firstAction = (Element)actions.item(1);
//            Element triggers = (Element)firstAction.getElementsByTagName("triggers").item(0);
//            // Get the first trigger phrase
//            String firstTriggerPhrase = triggers.getElementsByTagName("keyphrase").item(0).getTextContent();
//            assertEquals("open", firstTriggerPhrase, "First trigger phrase was not 'open'");
//        } catch(ParserConfigurationException pce) {
//            fail("ParserConfigurationException was thrown when attempting to read basic actions file");
//        } catch(SAXException saxe) {
//            fail("SAXException was thrown when attempting to read basic actions file");
//        } catch(IOException ioe) {
//            fail("IOException was thrown when attempting to read basic actions file");
//        }
//    }
}
