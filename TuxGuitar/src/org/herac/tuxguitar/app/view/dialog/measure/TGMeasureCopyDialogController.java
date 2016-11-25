package org.herac.tuxguitar.app.view.dialog.measure;

import org.herac.tuxguitar.app.view.component.tab.Selector;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.controller.TGOpenViewController;

public class TGMeasureCopyDialogController implements TGOpenViewController {

	private final Selector selector;

	public TGMeasureCopyDialogController(Selector selector) {
		this.selector = selector;
	}

	public void openView(TGViewContext context) {
		context.setAttribute(TGMeasureCopyDialog.ATTRIBUTE_SELECTOR, selector);
		new TGMeasureCopyDialog().show(context);
	}
}
