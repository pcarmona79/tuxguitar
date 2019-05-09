package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UISize;

public interface UIWindow extends UILayoutContainer {
	
	String getText();

	void setText(String text);
	
	UIImage getImage();
	
	void setImage(UIImage image);
	
	void open();
	
	void close();
	
	void join();
	
	void minimize();
	
	void maximize();
	
	boolean isMaximized();

	void setMinimumSize(UISize size);

	void moveToTop();
	
	void addCloseListener(UICloseListener listener);
	
	void removeCloseListener(UICloseListener listener);
}
