package org.herac.tuxguitar.app.view.dialog.percussion;

import org.herac.tuxguitar.app.view.controller.TGToggleViewController;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGPercussionEditorController implements TGToggleViewController {

	public void toggleView(TGViewContext context) {
		TGPercussionEditor editor = TGPercussionEditor.getInstance(context.getContext());
		if( editor.isDisposed()){
			editor.show();
		} else {
			editor.dispose();
		}
	}
}
