package org.herac.tuxguitar.app.view.dialog.clipboard;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGNotePasteDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGNotePasteDialog().show(context);
	}
}
