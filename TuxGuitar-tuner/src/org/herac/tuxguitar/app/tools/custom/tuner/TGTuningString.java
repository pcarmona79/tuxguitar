/**
 * 
 */
package org.herac.tuxguitar.app.tools.custom.tuner;

import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIToggleButton;
// import org.herac.tuxguitar.ui.UIComponent;

/**
 * @author Nikola Kolarovic <johnny47ns@yahoo.com>
 *
 */
public class TGTuningString {

	private int string;
	private UIToggleButton stringButton = null;
	private TGTunerListener listener = null;
	
	
	
	TGTuningString(UIFactory factory, UIContainer parent, TGTunerListener listener, int string) {
		this.string = string;
		this.listener = listener;
		
		this.stringButton = factory.createToggleButton(parent, false);
		this.stringButton.setText("--------- "+TGTunerRoughWidget.TONESSTRING[string%12]+(int)Math.floor(string/12)+" ---------");
	}

	
	
	void addListener() {
		this.stringButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				// make unselect possible
				// UIComponent uiComponent = event.getComponent();
				// System.out.println( "instanceof UIToggleButton: " + (uiComponent instanceof UIToggleButton) + " : isSelected: " + ((UIToggleButton) uiComponent).isSelected());
				// System.out.println( "StringButton isSelected(): " + TGTuningString.this.getStringButton().isSelected());
				// System.out.println( "StringButton getText(): " + TGTuningString.this.getStringButton().getText());
				// if( uiComponent instanceof UIToggleButton && ((UIToggleButton) uiComponent).isSelected() ) {
				// 	System.out.println("unselect");
				// 	TGTuningString.this.stringButton.setSelected(false);
				// 	TGTuningString.this.listener.fireCurrentString(0);
				// } else {
					System.out.println("select");
					TGTuningString.this.stringButton.setSelected(true);
					// TGTuningString.this.listener.fireCurrentString(TGTuningString.this.string);	
				// }
			}
		});
	}
	
	public int getString() {
		return this.string;
	}
	
	public UIToggleButton getStringButton() {
		return this.stringButton;
	}
	
}
