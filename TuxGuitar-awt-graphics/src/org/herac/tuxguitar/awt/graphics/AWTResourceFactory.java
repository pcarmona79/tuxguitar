package org.herac.tuxguitar.awt.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;
import org.herac.tuxguitar.util.TGException;

public class AWTResourceFactory implements UIResourceFactory {
	
	public AWTResourceFactory(){
		super();
	}

	public void loadFont(String font) {
	}

	public UIColor createColor(int red, int green, int blue ){
		return new AWTColor( red, green , blue );
	}
	
	public UIColor createColor( UIColorModel cm ){
		return this.createColor(cm.getRed(), cm.getGreen(), cm.getBlue());
	}
	
	public UIFont createFont( String name, float height, boolean bold, boolean italic ){
		return new AWTFont( name , height , bold , italic );
	}
	
	public UIFont createFont( UIFontModel fm ){
		return this.createFont(fm.getName(), fm.getHeight(), fm.isBold(), fm.isItalic());
	}
	
	public UIImage createImage( float width, float height ){
		return new AWTImage( width, height );
	}

	public UIImage createImage(Map<Integer, InputStream> inputStream) {
		try {
			return new AWTImage( inputStream.get(100) );
		} catch (IOException e) {
			throw new TGException();
		}
	}
}
