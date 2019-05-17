package org.herac.tuxguitar.ui.event;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIKeyCombination;

public class UIKeyEvent extends UIEvent {
	public static final int ALT = 1 << 16;
	public static final int SHIFT = 1 << 17;
	public static final int CTRL = 1 << 18;
	public static final int SUPER = 1 << 22;

	private UIKeyCombination keyCombination;
	
	public UIKeyEvent(UIComponent control, UIKeyCombination keyCombination) {
		super(control);
		
		this.keyCombination = keyCombination;
	}

	public UIKeyCombination getKeyCombination() {
		return keyCombination;
	}
}
