package org.herac.tuxguitar.app.view.dialog.piano;

import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.util.TGContext;

public class TGPianoEditorController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGContext innerContext = context.getContext();
		TGPianoEditor editor = TGPianoEditor.getInstance(innerContext);
		if( editor.isVisible()){
			editor.hideComponent();
		} else {
			editor.showComponent();
		}
		TGConfigManager tgConfig = TGConfigManager.getInstance(innerContext);
		tgConfig.setValue(TGConfigKeys.SHOW_PIANO, editor.isVisible());
	}
}
