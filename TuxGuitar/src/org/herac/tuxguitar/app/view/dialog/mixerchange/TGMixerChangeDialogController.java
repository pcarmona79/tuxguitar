package org.herac.tuxguitar.app.view.dialog.mixerchange;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGMixerChangeDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGMixerChangeDialog().show(context);
	}
}
