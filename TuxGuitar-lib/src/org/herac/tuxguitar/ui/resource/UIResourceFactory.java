package org.herac.tuxguitar.ui.resource;

import java.io.InputStream;
import java.util.Map;

public interface UIResourceFactory {

	void loadFont(String font);

	UIColor createColor(int red, int green, int blue);
	
	UIColor createColor(UIColorModel model);
	
	UIFont createFont(String name, float height, boolean bold, boolean italic);
	
	UIFont createFont(UIFontModel model);
	
	UIImage createImage(float width, float height);
	
	UIImage createImage(Map<Integer, InputStream> inputStreams);
}
