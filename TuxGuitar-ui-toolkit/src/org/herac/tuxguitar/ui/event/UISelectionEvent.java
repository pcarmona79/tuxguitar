package org.herac.tuxguitar.ui.event;
import org.herac.tuxguitar.ui.UIComponent;

public class UISelectionEvent extends UIEvent {

	private Integer state;

	public UISelectionEvent(UIComponent control, Integer state) {
		super(control);

		this.state = state;
	}

	public UISelectionEvent(UIComponent control) {
		this(control, 0);
	}

	public Integer getState() {
		return state;
	}
}
