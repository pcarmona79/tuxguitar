package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.event.UIPaintListener;

public interface UICanvas extends UIControl {

	String getToolTipText();

	void setToolTipText(String text);

	void addPaintListener(UIPaintListener listener);
	
	void removePaintListener(UIPaintListener listener);
}
