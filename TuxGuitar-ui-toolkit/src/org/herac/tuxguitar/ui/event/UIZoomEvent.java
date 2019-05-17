package org.herac.tuxguitar.ui.event;

import org.herac.tuxguitar.ui.UIComponent;

public class UIZoomEvent extends UIEvent {
	
	private Integer value;
	private Integer state;

	public UIZoomEvent(UIComponent control, Integer value, Integer state) {
		super(control);
		
		this.value = value;
		this.state = state;
	}

	public Integer getValue() {
		return value;
	}

	public Integer getState() {
		return state;
	}
}
