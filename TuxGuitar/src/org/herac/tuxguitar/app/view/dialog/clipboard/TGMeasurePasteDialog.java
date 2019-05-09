package org.herac.tuxguitar.app.view.dialog.clipboard;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.editor.action.measure.TGPasteMeasureAction;

public class TGMeasurePasteDialog extends TGPasteDialog {

	public TGMeasurePasteDialog() {
		super(TGPasteMeasureAction.NAME);
	}

	@Override
	protected String getReplaceText() {
		return TuxGuitar.getProperty("edit.paste.replace-mode");
	}

	@Override
	protected String getInsertText() {
		return TuxGuitar.getProperty("edit.paste.insert-mode");
	}

	@Override
	protected String getTitle() {
		return TuxGuitar.getProperty("measure.paste");
	}
}
