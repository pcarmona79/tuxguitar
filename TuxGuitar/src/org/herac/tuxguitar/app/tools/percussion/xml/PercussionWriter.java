package org.herac.tuxguitar.app.tools.percussion.xml;

import org.herac.tuxguitar.app.tools.percussion.PercussionEntry;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;

import static org.herac.tuxguitar.app.tools.percussion.xml.PercussionReader.*;

public class PercussionWriter {
    private static final String PERCUSSION_ROOT = "percussion";

    public static void write(PercussionEntry[] entries, String fileName) {
        try {
            File file = new File(fileName);
            Document doc = createDocument();
            write(entries, doc);
            saveDocument(doc, file);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static Document createDocument() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            return document;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public static void saveDocument(Document document, File file) {
        try {
            FileOutputStream fs = new FileOutputStream(file);

            // Write it out again
            TransformerFactory xformFactory = TransformerFactory.newInstance();
            Transformer idTransform = xformFactory.newTransformer();
            Source input = new DOMSource(document);
            Result output = new StreamResult(fs);
            idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
            idTransform.transform(input, output);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void write(PercussionEntry[] entries, Document document) {
        Node shortcutsNode = document.createElement(PERCUSSION_ROOT);

        for (int i = 0; i < entries.length; i++) {
            PercussionEntry entry = entries[i];

            Node node = document.createElement(ENTRY_TAG);
            shortcutsNode.appendChild(node);

            Attr attrName = document.createAttribute(NAME_ATTRIBUTE);
            Attr attrShow = document.createAttribute(SHOW_ATTRIBUTE);
            Attr attrMidi = document.createAttribute(MIDI_ATTRIBUTE);
            Attr attrScore = document.createAttribute(SCORE_ATTRIBUTE);
            Attr attrKind = document.createAttribute(KIND_ATTRIBUTE);

            attrName.setNodeValue(entry.getName());
            attrShow.setNodeValue(Boolean.toString(entry.isShown()));
            attrMidi.setNodeValue(Integer.toString(i));
            attrScore.setNodeValue(Integer.toString(entry.getNote()));
            attrKind.setNodeValue(Integer.toString(entry.getKind()));

            node.getAttributes().setNamedItem(attrName);
            node.getAttributes().setNamedItem(attrShow);
            node.getAttributes().setNamedItem(attrMidi);
            node.getAttributes().setNamedItem(attrScore);
            node.getAttributes().setNamedItem(attrKind);
        }
        document.appendChild(shortcutsNode);
    }
}
