package org.herac.tuxguitar.app.view.dialog.scale;

import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGScaleDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		TGScaleDialog editor = TGScaleDialog.getInstance(context.getContext());
		if( editor.isDisposed()){
			editor.show(context);
		} else {
			editor.dispose();
		}
	}
}
