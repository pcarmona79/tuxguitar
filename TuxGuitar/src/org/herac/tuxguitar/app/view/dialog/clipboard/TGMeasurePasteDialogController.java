package org.herac.tuxguitar.app.view.dialog.clipboard;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.dialog.clipboard.TGMeasurePasteDialog;

public class TGMeasurePasteDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		new TGMeasurePasteDialog().show(context);
	}
}
