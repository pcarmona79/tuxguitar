package org.herac.tuxguitar.song.helpers.tuning.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.herac.tuxguitar.song.helpers.tuning.TuningGroup;
import org.herac.tuxguitar.song.helpers.tuning.TuningPreset;

import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TuningReader {
	public static final String TUNING_TAG = "tuning";
	public static final String GROUP_TAG = "group";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String NOTES_ATTRIBUTE = "notes";
	public static final String PROGRAM_ATTRIBUTE = "program";
	public static final String CLEF_ATTRIBUTE = "clef";
	public static final String FRETS_ATTRIBUTE = "frets";
	public static final String KEY_SEPARATOR = ",";

	public void loadTunings(TuningGroup group, InputStream stream){
		try{
			if ( stream != null ){
				Document doc = getDocument(stream);
				loadTunings(group, doc.getFirstChild());
			}
		}catch(Throwable e){
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
	
	private static void loadTunings(TuningGroup group, Node node){
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			String nodeName = child.getNodeName();
			
			if (nodeName.equals(TUNING_TAG)) {
				NamedNodeMap params = child.getAttributes();
				
				String name = params.getNamedItem(NAME_ATTRIBUTE).getNodeValue();
				String notes = params.getNamedItem(NOTES_ATTRIBUTE).getNodeValue();
				String program = params.getNamedItem(PROGRAM_ATTRIBUTE).getNodeValue();
				String clef = params.getNamedItem(CLEF_ATTRIBUTE).getNodeValue();
				String frets = params.getNamedItem(FRETS_ATTRIBUTE).getNodeValue();

				if (name == null || notes == null || name.trim().equals("") || notes.trim().equals("")){
					throw new RuntimeException("Invalid Tuning file format.");
				}
				String[] noteStrings = notes.split(KEY_SEPARATOR);
				int[] noteValues = new int[noteStrings.length];
				for (int j = 0; j < noteStrings.length; j++){
					int note = Integer.parseInt(noteStrings[j]);
					if( note >= 0 && note < 128 ){
						noteValues[j] = note;
					} else {
						throw new RuntimeException("Invalid Tuning note: " + noteStrings[j]);
					}
				}

				short programNumber = program == null ? TGChannel.DEFAULT_PROGRAM : Short.parseShort(program);
				int clefValue = clef == null ? TGMeasure.DEFAULT_CLEF : Integer.parseInt(clef);
				int fretValue = frets == null ? TGTrack.DEFAULT_FRETS : Integer.parseInt(frets);
				TuningPreset tuning = new TuningPreset(group, name, noteValues, programNumber, clefValue, fretValue);
				group.getTunings().add(tuning);
			} else if (nodeName.equals(GROUP_TAG)) {
				NamedNodeMap params = child.getAttributes();
				String name = params.getNamedItem(NAME_ATTRIBUTE).getNodeValue();

				TuningGroup subGroup = new TuningGroup(group, name);
				loadTunings(subGroup, child);
				group.getGroups().add(subGroup);
			}
		}
	}
}
