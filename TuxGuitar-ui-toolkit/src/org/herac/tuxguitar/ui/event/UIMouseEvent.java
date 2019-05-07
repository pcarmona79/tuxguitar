package org.herac.tuxguitar.ui.event;
import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIPosition;

public class UIMouseEvent extends UIEvent {
	
	private Integer button;
	private UIPosition position;
	private Integer state;
	
	public UIMouseEvent(UIComponent control, UIPosition position, Integer button, Integer state) {
		super(control);
		
		this.button = button;
		this.position = position;
		this.state = state;
	}

	public UIPosition getPosition() {
		return position;
	}

	public Integer getButton() {
		return button;
	}

	public Integer getState() {
		return state;
	}
}
