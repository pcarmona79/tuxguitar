package org.herac.tuxguitar.app.view.menu;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBar;

public class TGMainMenuController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TuxGuitar.getInstance().getItemManager().toggleMainMenuVisibility();
	}
}
