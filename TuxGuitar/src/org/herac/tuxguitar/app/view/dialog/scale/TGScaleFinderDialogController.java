package org.herac.tuxguitar.app.view.dialog.scale;

import org.herac.tuxguitar.app.view.controller.TGOpenViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGScaleFinderDialogController implements TGOpenViewController {

	public void openView(TGViewContext context) {
		TGScaleFinderDialog editor = TGScaleFinderDialog.getInstance(context.getContext());
		if( editor.isDisposed()){
			editor.show(context);
		} else {
			editor.dispose();
		}
	}
}
