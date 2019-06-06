package org.herac.tuxguitar.app.tools.percussion.xml;

import org.herac.tuxguitar.app.tools.percussion.PercussionEntry;
import org.herac.tuxguitar.graphics.control.TGDrumMap;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class PercussionReader {

    public static final String ENTRY_TAG = "entry";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String SHOW_ATTRIBUTE = "show-in-editor";
    public static final String MIDI_ATTRIBUTE = "midi-note";
    public static final String POSITION_ATTRIBUTE = "score-position";
    public static final String KIND_ATTRIBUTE = "display-as";

    public static final int POSITION_MIN = -32;
    public static final int POSITION_MAX = 32;

    public static void loadPercussion(PercussionEntry[] entries, InputStream stream) {
        try {
            if (stream != null) {
                Document doc = getDocument(stream);
                loadPercussion(entries, doc.getFirstChild());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static Document getDocument(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(stream);

        return document;
    }

    private static void loadPercussion(PercussionEntry[] entries, Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            String nodeName = child.getNodeName();

            if (nodeName.equals(ENTRY_TAG)) {
                NamedNodeMap params = child.getAttributes();

                String name = params.getNamedItem(NAME_ATTRIBUTE).getNodeValue();
                String show = params.getNamedItem(SHOW_ATTRIBUTE).getNodeValue();
                String midi = params.getNamedItem(MIDI_ATTRIBUTE).getNodeValue();
                String position = params.getNamedItem(POSITION_ATTRIBUTE).getNodeValue();
                String kind = params.getNamedItem(KIND_ATTRIBUTE).getNodeValue();

                boolean showValue = Boolean.parseBoolean(show);
                int midiValue = Math.min(Math.max(Integer.parseInt(midi), 0), TGDrumMap.MAX_NOTES);
                int positionValue = Math.min(Math.max(Integer.parseInt(position), POSITION_MIN), POSITION_MAX);
                int kindValue = Integer.parseInt(kind);

                entries[midiValue] = new PercussionEntry(name == null ? "" : name, positionValue, kindValue, showValue);
            }
        }
    }
}
