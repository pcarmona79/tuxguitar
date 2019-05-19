package org.herac.tuxguitar.app.tools.scale.xml;

import org.herac.tuxguitar.app.tools.scale.ScaleInfo;
import org.herac.tuxguitar.song.models.TGScale;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.herac.tuxguitar.app.tools.scale.xml.ScaleReader.*;

public class ScaleWriter {
    private static final String SCALES_ROOT = "scales";

    public static void write(Collection<ScaleInfo> scales, String fileName) {
        try {
            File file = new File(fileName);
            Document doc = createDocument();
            write(scales, doc);
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

    private static void write(Collection<ScaleInfo> scales, Document document) {
        Node shortcutsNode = document.createElement(SCALES_ROOT);

        for (ScaleInfo scale : scales) {
            Node node = document.createElement(SCALE_TAG);
            shortcutsNode.appendChild(node);

            Attr attrName = document.createAttribute(NAME_ATTRIBUTE);
            Attr attrKeys = document.createAttribute(KEYS_ATTRIBUTE);

            List<String> keyStrings = new ArrayList<>();
            for (int i = 0; i < TGScale.NOTE_COUNT; i++) {
                if ((scale.getKeys() & (1 << i)) != 0) {
                    keyStrings.add(Integer.toString(i + 1));
                }
            }

            attrName.setNodeValue(scale.getName());
            attrKeys.setNodeValue(String.join(KEY_SEPARATOR, keyStrings));

            node.getAttributes().setNamedItem(attrName);
            node.getAttributes().setNamedItem(attrKeys);
        }
        document.appendChild(shortcutsNode);
    }
}
