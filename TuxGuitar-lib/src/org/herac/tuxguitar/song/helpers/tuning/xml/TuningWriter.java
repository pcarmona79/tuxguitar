package org.herac.tuxguitar.song.helpers.tuning.xml;

import org.herac.tuxguitar.song.helpers.tuning.TuningGroup;
import org.herac.tuxguitar.song.helpers.tuning.TuningPreset;
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
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.herac.tuxguitar.song.helpers.tuning.xml.TuningReader.*;

public class TuningWriter {
    private static final String TUNINGS_ROOT = "tunings";

    public static void write(TuningGroup group, String fileName) {
        try {
            File file = new File(fileName);
            Document doc = createDocument();
            write(group, doc);
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

    private static void write(TuningGroup group, Document document) {
        Node root = document.createElement(TUNINGS_ROOT);
        write(group, root);
        document.appendChild(root);
    }

    private static void write(TuningGroup group, Node parent) {
        Document document = parent.getOwnerDocument();
        if (group.getName() != null && !group.getName().isEmpty()) {
            Attr attrName = document.createAttribute(NAME_ATTRIBUTE);
            attrName.setNodeValue(group.getName());
            parent.getAttributes().setNamedItem(attrName);
        }
        for (TuningGroup child : group.getGroups()) {
            Node node = document.createElement(GROUP_TAG);
            write(child, node);
            parent.appendChild(node);
        }
        for (TuningPreset preset : group.getTunings()) {
            Node node = document.createElement(TUNING_TAG);

            Attr attrName = document.createAttribute(NAME_ATTRIBUTE);
            Attr attrNotes = document.createAttribute(NOTES_ATTRIBUTE);
            Attr attrProgram = document.createAttribute(PROGRAM_ATTRIBUTE);
            Attr attrClef = document.createAttribute(CLEF_ATTRIBUTE);

            attrName.setNodeValue(preset.getName());
            attrNotes.setNodeValue(Arrays.stream(preset.getValues()).mapToObj(Integer::toString).collect(Collectors.joining(",")));
            attrProgram.setNodeValue(Integer.toString(preset.getProgram()));
            attrClef.setNodeValue(Integer.toString(preset.getClef()));

            node.getAttributes().setNamedItem(attrName);
            node.getAttributes().setNamedItem(attrNotes);
            node.getAttributes().setNamedItem(attrProgram);
            node.getAttributes().setNamedItem(attrClef);

            parent.appendChild(node);
        }
    }
}
