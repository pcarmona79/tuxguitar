package org.herac.tuxguitar.app.tools.scale.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.herac.tuxguitar.app.tools.scale.ScaleInfo;
import org.herac.tuxguitar.song.models.TGScale;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ScaleReader {
	public static final String SCALE_TAG = "scale";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String KEYS_ATTRIBUTE = "keys";
	public static final String KEY_SEPARATOR = ",";

	public static void loadScales(List<ScaleInfo> scales,InputStream stream){
		try{
			if ( stream != null ){
				Document doc = getDocument(stream);
				loadScales(scales,doc.getFirstChild());
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
	
	private static void loadScales(List<ScaleInfo> scales,Node node){
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			String nodeName = child.getNodeName();
			
			if (nodeName.equals(SCALE_TAG)) {
				NamedNodeMap params = child.getAttributes();
				
				String name = params.getNamedItem(NAME_ATTRIBUTE).getNodeValue();
				String keys = params.getNamedItem(KEYS_ATTRIBUTE).getNodeValue();
				
				if (name == null || keys == null || name.trim().equals("") || keys.trim().equals("")){
					throw new RuntimeException("Invalid Scale file format.");
				}

				int keyFlags = 0;
                for (String keyString : keys.split(KEY_SEPARATOR)) {
					int note = (Integer.parseInt(keyString) - 1);
					if( note >= 0 && note < TGScale.NOTE_COUNT) {
						keyFlags |= (1 << note);
					}
				}
				scales.add(new ScaleInfo(name, keyFlags));
			}
		}
	}
}
